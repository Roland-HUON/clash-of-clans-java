package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.dto.PlayerTroopDto;
import com.rolandhuon.clashofclans.dto.VillageBuildingDto;
import com.rolandhuon.clashofclans.service.PlayerService;
import com.rolandhuon.clashofclans.service.UpgradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UpgradeController {

    private final UpgradeService upgradeService;
    private final PlayerService playerService;

    public UpgradeController(UpgradeService upgradeService, PlayerService playerService) {
        this.upgradeService = upgradeService;
        this.playerService = playerService;
    }

    @GetMapping("/api/players/{playerId}/troops")
    public List<PlayerTroopDto> troopsOf(@PathVariable Long playerId) {
        return playerService.findTroopsOf(playerId).stream()
                .map(PlayerTroopDto::from)
                .toList();
    }

    @PostMapping("/api/buildings/{id}/upgrade")
    public VillageBuildingDto upgradeBuilding(@PathVariable Long id) {
        return VillageBuildingDto.from(upgradeService.upgradeBuilding(id));
    }

    @PostMapping("/api/troops/{id}/upgrade")
    public PlayerTroopDto upgradeTroop(@PathVariable Long id) {
        return PlayerTroopDto.from(upgradeService.upgradeTroop(id));
    }
}
