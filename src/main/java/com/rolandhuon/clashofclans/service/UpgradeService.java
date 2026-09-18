package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.exceptions.NotFoundException;
import com.rolandhuon.clashofclans.exceptions.InsufficientResourcesException;
import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.ResourceType;
import com.rolandhuon.clashofclans.domain.village.Harvest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerTroopRepository;
import com.rolandhuon.clashofclans.repository.VillageBuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class UpgradeService {

    private final VillageBuildingRepository buildingRepository;
    private final PlayerTroopRepository troopRepository;
    private final Clock clock;

    public UpgradeService(VillageBuildingRepository buildingRepository,
                          PlayerTroopRepository troopRepository,
                          Clock clock) {
        this.buildingRepository = buildingRepository;
        this.troopRepository = troopRepository;
        this.clock = clock;
    }

    @Transactional
    public VillageBuilding upgradeBuilding(Long buildingId) {
        VillageBuilding building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building", buildingId));

        bankWhatTheOldLevelProduced(building);

        Player owner = building.getVillage().getPlayer();
        int nextLevel = pay(owner, building.getType(), building.getLevel());
        building.setLevel(nextLevel);

        return buildingRepository.save(building);
    }

    @Transactional
    public PlayerTroop upgradeTroop(Long troopId) {
        PlayerTroop troop = troopRepository.findById(troopId)
                .orElseThrow(() -> new NotFoundException("Troop", troopId));

        Player owner = troop.getPlayer();
        int nextLevel = pay(owner, troop.getType(), troop.getLevel());
        troop.setLevel(nextLevel);

        return troopRepository.save(troop);
    }

    private void bankWhatTheOldLevelProduced(VillageBuilding building) {
        if (!building.getType().produces()) return;

        Harvest harvest = Harvest.nothing()
                .plus(building.getType().producedResource(), building.collect(clock.instant()));
        if (harvest.isEmpty()) return;

        Village village = building.getVillage();
        village.stock(harvest.gold(), harvest.elixir(), harvest.darkElixir());
        village.getPlayer().earn(harvest.gold(), harvest.elixir(), harvest.darkElixir());
    }

    private int pay(Player owner, EntityType type, int currentLevel) {
        if (currentLevel >= type.maxLevel()) {
            throw new IllegalStateException(type.label() + " is already at max level (" + type.maxLevel() + ")");
        }

        ResourceType resource = type.upgradeResource();
        int cost = type.upgradeCostFrom(currentLevel);
        long balance = owner.balanceOf(resource);

        if (balance < cost) {
            throw new InsufficientResourcesException(resource.name().toLowerCase().replace('_', ' '), balance, cost);
        }

        owner.spend(resource, cost);
        return currentLevel + 1;
    }
}
