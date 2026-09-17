package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.dto.AddBuildingRequest;
import com.rolandhuon.clashofclans.dto.VillageRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import com.rolandhuon.clashofclans.repository.VillageBuildingRepository;
import com.rolandhuon.clashofclans.repository.VillageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VillageService {

    private final VillageRepository villageRepository;
    private final PlayerRepository playerRepository;
    private final VillageBuildingRepository buildingRepository;

    public VillageService(VillageRepository villageRepository,
                          PlayerRepository playerRepository,
                          VillageBuildingRepository buildingRepository) {
        this.villageRepository = villageRepository;
        this.playerRepository = playerRepository;
        this.buildingRepository = buildingRepository;
    }

    public List<Village> findByPlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) throw new PlayerNotFoundException(playerId);
        return villageRepository.findByPlayerId(playerId);
    }

    public Village findById(Long id) {
        return villageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Village", id));
    }

    @Transactional
    public Village create(Long playerId, VillageRequest request) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        Village village = new Village(request.name(), request.gold(), request.elixir(), request.darkElixir());
        player.addVillage(village);

        return villageRepository.save(village);
    }

    @Transactional
    public VillageBuilding addBuilding(Long villageId, AddBuildingRequest request) {
        Village village = findById(villageId);
        BuildingType type = BuildingType.valueOf(request.type().toUpperCase());

        long already = village.getBuildings().stream()
                .filter(b -> b.getType() == type)
                .count();

        if (already >= type.maxCount()) {
            throw new IllegalStateException("Too many " + type.label() + " (max " + type.maxCount() + ")");
        }

        VillageBuilding building = new VillageBuilding(type, request.level());
        village.addBuilding(building);

        return buildingRepository.save(building);
    }

    @Transactional
    public void delete(Long id) {
        if (!villageRepository.existsById(id)) throw new NotFoundException("Village", id);
        villageRepository.deleteById(id);
    }
}
