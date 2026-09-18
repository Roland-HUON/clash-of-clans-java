package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.village.Harvest;
import com.rolandhuon.clashofclans.model.Village;
import com.rolandhuon.clashofclans.model.VillageBuilding;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import com.rolandhuon.clashofclans.repository.VillageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class ProductionService {

    private final VillageRepository villageRepository;
    private final PlayerRepository playerRepository;
    private final Clock clock;

    public ProductionService(VillageRepository villageRepository,
                             PlayerRepository playerRepository,
                             Clock clock) {
        this.villageRepository = villageRepository;
        this.playerRepository = playerRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Harvest pending(Village village) {
        Instant now = clock.instant();
        Harvest harvest = Harvest.nothing();

        for (VillageBuilding building : village.getBuildings()) {
            if (!building.getType().produces()) continue;
            harvest = harvest.plus(building.getType().producedResource(), building.pendingProduction(now));
        }
        return harvest;
    }

    @Transactional
    public Harvest collect(Long villageId) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new NotFoundException("Village", villageId));

        Instant now = clock.instant();
        Harvest harvest = Harvest.nothing();

        for (VillageBuilding building : village.getBuildings()) {
            if (!building.getType().produces()) continue;
            harvest = harvest.plus(building.getType().producedResource(), building.collect(now));
        }

        if (harvest.isEmpty()) return harvest;

        village.stock(harvest.gold(), harvest.elixir(), harvest.darkElixir());
        village.getPlayer().earn(harvest.gold(), harvest.elixir(), harvest.darkElixir());

        villageRepository.save(village);
        playerRepository.save(village.getPlayer());

        return harvest;
    }
}
