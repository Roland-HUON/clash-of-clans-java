package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.BattleKind;
import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.battle.Loot;
import com.rolandhuon.clashofclans.domain.battle.TrophyExchange;
import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.dto.RaidRequest;
import com.rolandhuon.clashofclans.dto.RaidUnitRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import com.rolandhuon.clashofclans.repository.PlayerTroopRepository;
import com.rolandhuon.clashofclans.repository.VillageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RaidService {

    private final PlayerRepository playerRepository;
    private final VillageRepository villageRepository;
    private final PlayerTroopRepository troopRepository;
    private final TroopFactory troopFactory;
    private final BuildingFactory buildingFactory;
    private final BattleService battleService;
    private final BattleHistoryService battleHistoryService;

    public RaidService(PlayerRepository playerRepository,
                       VillageRepository villageRepository,
                       PlayerTroopRepository troopRepository,
                       TroopFactory troopFactory,
                       BuildingFactory buildingFactory,
                       BattleService battleService,
                       BattleHistoryService battleHistoryService) {
        this.playerRepository = playerRepository;
        this.villageRepository = villageRepository;
        this.troopRepository = troopRepository;
        this.troopFactory = troopFactory;
        this.buildingFactory = buildingFactory;
        this.battleService = battleService;
        this.battleHistoryService = battleHistoryService;
    }

    @Transactional
    public RaidOutcome raid(RaidRequest request) {
        Player attacker = playerRepository.findById(request.attackerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.attackerId()));

        Village base = villageRepository.findById(request.attackerVillageId())
                .orElseThrow(() -> new NotFoundException("Village", request.attackerVillageId()));

        Village target = villageRepository.findById(request.targetVillageId())
                .orElseThrow(() -> new NotFoundException("Village", request.targetVillageId()));

        Player defender = target.getPlayer();

        if (!base.getPlayer().getId().equals(attacker.getId())) {
            throw new IllegalStateException(
                    "Village " + base.getId() + " does not belong to player " + attacker.getId());
        }
        if (defender.getId().equals(attacker.getId())) {
            throw new IllegalStateException("A player cannot raid their own village.");
        }
        if (target.getBuildings().isEmpty()) {
            throw new IllegalStateException("Village " + target.getId() + " has no building to attack.");
        }

        List<Troop> army = buildArmy(attacker, base, request.army());
        com.rolandhuon.clashofclans.domain.village.Village battlefield = buildBattlefield(target);

        BattleResult result = battleService.fight(army, battlefield);

        Loot loot = Loot.proportionalTo(
                result.destructionPercentage(),
                target.getGold(), target.getElixir(), target.getDarkElixir());

        target.loot(loot.gold(), loot.elixir(), loot.darkElixir());
        attacker.earn(loot.gold(), loot.elixir(), loot.darkElixir());

        TrophyExchange trophies = TrophyExchange.forStars(result.stars())
                .cappedBy(attacker.getTrophies(), defender.getTrophies());
        attacker.applyTrophyDelta(trophies.attackerDelta());
        defender.applyTrophyDelta(trophies.defenderDelta());

        villageRepository.save(target);
        playerRepository.save(attacker);
        playerRepository.save(defender);
        battleHistoryService.save(BattleKind.RAID, result);

        return new RaidOutcome(result, loot, trophies, attacker.getTrophies(), defender.getTrophies());
    }

    private int housingCapacityOf(Village base) {
        return base.getBuildings().stream()
                .filter(b -> b.getType().storesTroops())
                .mapToInt(b -> b.getType().housingCapacityAt(b.getLevel()))
                .sum();
    }

    private List<Troop> buildArmy(Player attacker, Village base, List<RaidUnitRequest> spec) {
        if (spec == null || spec.isEmpty()) {
            throw new IllegalArgumentException("An army is required.");
        }

        int capacity = housingCapacityOf(base);
        if (capacity == 0) {
            throw new IllegalStateException("Village " + base.getId() + " has no military camp to host an army.");
        }

        List<Troop> army = new ArrayList<>();
        for (RaidUnitRequest unit : spec) {
            TroopType type = TroopType.from(unit.type());

            PlayerTroop researched = troopRepository.findByPlayerIdAndType(attacker.getId(), type)
                    .orElseThrow(() -> new IllegalStateException(
                            "Player " + attacker.getId() + " has not unlocked " + type.label()));

            army.addAll(troopFactory.create(type, researched.getLevel(), unit.count()));
        }

        int used = army.stream().mapToInt(Troop::getHousingSpace).sum();
        if (used > capacity) {
            throw new IllegalArgumentException(
                    "Army too large: " + used + " housing space used, " + capacity + " available.");
        }

        return army;
    }

    private com.rolandhuon.clashofclans.domain.village.Village buildBattlefield(Village target) {
        var battlefield = new com.rolandhuon.clashofclans.domain.village.Village();

        for (VillageBuilding stored : target.getBuildings()) {
            Building building = buildingFactory.create(stored.getType(), stored.getLevel());
            battlefield.addBuilding(building);
        }
        return battlefield;
    }
}
