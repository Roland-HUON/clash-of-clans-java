package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.dto.AddBuildingRequest;
import com.rolandhuon.clashofclans.dto.VillageBuildingDto;
import com.rolandhuon.clashofclans.dto.VillageDto;
import com.rolandhuon.clashofclans.dto.VillageRequest;
import com.rolandhuon.clashofclans.service.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VillageController {

    private final VillageService villageService;

    public VillageController(VillageService villageService) {
        this.villageService = villageService;
    }

    @GetMapping("/api/players/{playerId}/villages")
    public List<VillageDto> byPlayer(@PathVariable Long playerId) {
        return villageService.findByPlayer(playerId).stream()
                .map(VillageDto::from)
                .toList();
    }

    @PostMapping("/api/players/{playerId}/villages")
    @ResponseStatus(HttpStatus.CREATED)
    public VillageDto create(@PathVariable Long playerId, @RequestBody VillageRequest request) {
        return VillageDto.from(villageService.create(playerId, request));
    }

    @GetMapping("/api/villages/{id}")
    public VillageDto find(@PathVariable Long id) {
        return VillageDto.from(villageService.findById(id));
    }

    @DeleteMapping("/api/villages/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        villageService.delete(id);
    }

    @PostMapping("/api/villages/{villageId}/buildings")
    @ResponseStatus(HttpStatus.CREATED)
    public VillageBuildingDto addBuilding(@PathVariable Long villageId, @RequestBody AddBuildingRequest request) {
        return VillageBuildingDto.from(villageService.addBuilding(villageId, request));
    }
}
