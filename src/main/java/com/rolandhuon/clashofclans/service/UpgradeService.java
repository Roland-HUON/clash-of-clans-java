package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.ResourceType;
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

    private int pay(Player owner, EntityType type, int currentLevel) {
        if (currentLevel >= type.maxLevel()) {
            throw new IllegalStateException(type.label() + " is already at max level (" + type.maxLevel() + ")");
        }

        ResourceType resource = type.upgradeResource();
        int cost = type.upgradeCostFrom(currentLevel);
        int balance = owner.balanceOf(resource);

        if (balance < cost) {
            throw new InsufficientResourcesException(resource.name().toLowerCase().replace('_', ' '), balance, cost);
        }

        owner.spend(resource, cost);
        return currentLevel + 1;
    }
}
