package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.rolandhuon.clashofclans.dto.PlayerTroopDto;
import com.rolandhuon.clashofclans.dto.VillageBuildingDto;
import com.rolandhuon.clashofclans.service.PlayerService;
import com.rolandhuon.clashofclans.service.UpgradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Upgrades", description = "Spending resources to raise a building or a troop by one level.")
@RestController
public class UpgradeController {

    private final UpgradeService upgradeService;
    private final PlayerService playerService;

    public UpgradeController(UpgradeService upgradeService, PlayerService playerService) {
        this.upgradeService = upgradeService;
        this.playerService = playerService;
    }

        @Operation(summary = "List a player's troops",
            description = "Returns every troop type the chief has unlocked, with its level, hit points, damage and the cost of its next upgrade.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/players/{playerId}/troops")
    public List<PlayerTroopDto> troopsOf(@PathVariable Long playerId) {
        return playerService.findTroopsOf(playerId).stream()
                .map(PlayerTroopDto::from)
                .toList();
    }

        @Operation(summary = "Upgrade a building",
            description = "Raises the building by one level and charges the owner in that building's own currency: each building is charged in the currency its own type declares, which is not always the one it produces - a Gold Mine is paid for in elixir, an Elixir Collector in gold.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The building went up a level and is returned with its new stats."),
            @ApiResponse(responseCode = "400", description = "The building is already at its maximum level."),
            @ApiResponse(responseCode = "404", description = "No building carries that id."),
            @ApiResponse(responseCode = "409", description = "The owner cannot afford the upgrade."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping("/api/buildings/{id}/upgrade")
    public VillageBuildingDto upgradeBuilding(@PathVariable Long id) {
        return VillageBuildingDto.from(upgradeService.upgradeBuilding(id));
    }

        @Operation(summary = "Upgrade a troop",
            description = "Raises the troop by one level and charges the owner. Every raid from now on uses the new level.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The troop went up a level and is returned with its new stats."),
            @ApiResponse(responseCode = "400", description = "The troop is already at its maximum level."),
            @ApiResponse(responseCode = "404", description = "No troop carries that id."),
            @ApiResponse(responseCode = "409", description = "The owner cannot afford the upgrade."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping("/api/troops/{id}/upgrade")
    public PlayerTroopDto upgradeTroop(@PathVariable Long id) {
        return PlayerTroopDto.from(upgradeService.upgradeTroop(id));
    }
}
