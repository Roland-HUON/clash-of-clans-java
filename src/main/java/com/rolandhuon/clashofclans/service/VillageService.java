package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.dto.BuildingRequest;
import com.rolandhuon.clashofclans.dto.VillageRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.domain.common.ResourceType;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import com.rolandhuon.clashofclans.repository.VillageBuildingRepository;
import com.rolandhuon.clashofclans.repository.VillageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
public class VillageService {

    private final VillageRepository villageRepository;
    private final PlayerRepository playerRepository;
    private final Clock clock;
    private final VillageBuildingRepository buildingRepository;

    public VillageService(VillageRepository villageRepository,
                          PlayerRepository playerRepository,
                          Clock clock,
                          VillageBuildingRepository buildingRepository) {
        this.villageRepository = villageRepository;
        this.playerRepository = playerRepository;
        this.clock = clock;
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    public List<Village> findAll() {
        return villageRepository.findAllByOrderByIdAsc().stream().map(this::hydrate).toList();
    }

    @Transactional(readOnly = true)
    public List<Village> findByPlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) throw new PlayerNotFoundException(playerId);

        List<Village> villages = villageRepository.findByPlayerId(playerId);
        villages.forEach(this::hydrate);
        return villages;
    }

    @Transactional(readOnly = true)
    public Village findById(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Village", id));
        return hydrate(village);
    }

    @Transactional
    public Village create(Long playerId, VillageRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        Village village = new Village(request.name(), request.gold(), request.elixir(), request.darkElixir());
        player.addVillage(village);

        return hydrate(villageRepository.save(village));
    }

    @Transactional
    public VillageBuilding addBuilding(Long villageId, BuildingRequest request) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new NotFoundException("Village", villageId));

        BuildingType type = BuildingType.from(request.type());
        payForBuilding(village.getPlayer(), type, request.level());

        VillageBuilding building = new VillageBuilding(type, request.level(), clock.instant());
        village.addBuilding(building);

        return buildingRepository.save(building);
    }

    @Transactional
    public void delete(Long id) {
        if (!villageRepository.existsById(id)) throw new NotFoundException("Village", id);
        villageRepository.deleteById(id);
    }

    private void payForBuilding(Player owner, BuildingType type, int level) {
        ResourceType resource = type.upgradeResource();
        int cost = type.costUpTo(level);
        long balance = owner.balanceOf(resource);

        if (balance < cost) {
            throw new InsufficientResourcesException(resource.name().toLowerCase().replace('_', ' '), balance, cost);
        }

        owner.spend(resource, cost);
        playerRepository.save(owner);
    }

    private Village hydrate(Village village) {
        village.getBuildings().size();
        return village;
    }
}
