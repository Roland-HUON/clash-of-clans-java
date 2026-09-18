package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.rolandhuon.clashofclans.dto.LeaderboardEntryDto;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Leaderboard", description = "Who is ahead on trophies.")
@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

        @Operation(summary = "Read the leaderboard",
            description = "Returns the players ordered by trophies, then by name. Without a limit it returns the top 10; limit must be between 1 and 100.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "400", description = "The limit is outside 1..100."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/leaderboard")
    public List<LeaderboardEntryDto> leaderboard(@RequestParam(required = false) Integer limit) {
        List<Player> ranking = leaderboardService.ranking(limit);

        List<LeaderboardEntryDto> entries = new ArrayList<>();
        for (int i = 0; i < ranking.size(); i++) {
            entries.add(LeaderboardEntryDto.of(i + 1, ranking.get(i)));
        }
        return entries;
    }

        @Operation(summary = "Read one player's rank",
            description = "Returns the chief's position in the same ranking, counting from 1.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/api/players/{id}/rank")
    public LeaderboardEntryDto rank(@PathVariable Long id) {
        return LeaderboardEntryDto.of(leaderboardService.rankOf(id), leaderboardService.find(id));
    }
}
