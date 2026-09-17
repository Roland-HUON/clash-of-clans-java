package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.PlayerTroop;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerTroopRepository;
import com.rolandhuon.clashofclans.repository.VillageBuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpgradeService {

    private final VillageBuildingRepository buildingRepository;
    private final PlayerTroopRepository troopRepository;

    public UpgradeService(VillageBuildingRepository buildingRepository, PlayerTroopRepository troopRepository) {
        this.buildingRepository = buildingRepository;
        this.troopRepository = troopRepository;
    }

    @Transactional
    public VillageBuilding upgradeBuilding(Long buildingId) {
        VillageBuilding building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building", buildingId));

        BuildingType type = building.getType();
        int current = building.getLevel();

        if (current >= type.maxLevel()) {
            throw new IllegalStateException(type.label() + " is already at max level (" + type.maxLevel() + ")");
        }

        int cost = type.upgradeCostFrom(current);
        Player owner = building.getVillage().getPlayer();

        if (owner.getGold() < cost) {
            throw new InsufficientResourcesException("gold", owner.getGold(), cost);
        }

        owner.spendGold(cost);
        building.setLevel(current + 1);

        return buildingRepository.save(building);
    }

    @Transactional
    public PlayerTroop upgradeTroop(Long troopId) {
        PlayerTroop troop = troopRepository.findById(troopId)
                .orElseThrow(() -> new NotFoundException("Troop", troopId));

        TroopType type = troop.getType();
        int current = troop.getLevel();

        if (current >= type.maxLevel()) {
            throw new IllegalStateException(type.label() + " is already at max level (" + type.maxLevel() + ")");
        }

        int cost = type.upgradeCostFrom(current);
        Player owner = troop.getPlayer();

        if (owner.getElixir() < cost) {
            throw new InsufficientResourcesException("elixir", owner.getElixir(), cost);
        }

        owner.spendElixir(cost);
        troop.setLevel(current + 1);

        return troopRepository.save(troop);
    }
}
