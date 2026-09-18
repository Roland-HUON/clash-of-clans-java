package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.rolandhuon.clashofclans.dto.RaidRequest;
import com.rolandhuon.clashofclans.dto.RaidResultDto;
import com.rolandhuon.clashofclans.service.RaidService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Raids", description = "Real attacks: one chief's army against another chief's village, for loot and trophies.")
@RestController
@RequestMapping("/api/raids")
public class RaidController {

    private final RaidService raidService;

    public RaidController(RaidService raidService) {
        this.raidService = raidService;
    }

    @Operation(summary = "Raid a village",
            description = "Sends an army from your village against another player's village. The defences fire back every turn, the loot taken is the destruction percentage applied to the defender's stock, and trophies change hands according to the stars. The army may not exceed the housing space of your military camps, and every troop type must already be unlocked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The raid was resolved; the result carries the stars, the loot and the trophy swing."),
            @ApiResponse(responseCode = "400", description = "Unknown troop type, empty army, army larger than your camps, a troop you have not unlocked, or a village that is not yours."),
            @ApiResponse(responseCode = "404", description = "The attacker, the attacking village or the target village does not exist."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping
    public RaidResultDto raid(@Valid @RequestBody RaidRequest request) {
        return RaidResultDto.from(raidService.raid(request));
    }
}
