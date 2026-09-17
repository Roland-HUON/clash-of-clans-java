package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.dto.LeaderboardEntryDto;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/api/leaderboard")
    public List<LeaderboardEntryDto> leaderboard(@RequestParam(required = false) Integer limit) {
        List<Player> ranking = leaderboardService.ranking(limit);

        List<LeaderboardEntryDto> entries = new ArrayList<>();
        for (int i = 0; i < ranking.size(); i++) {
            entries.add(LeaderboardEntryDto.of(i + 1, ranking.get(i)));
        }
        return entries;
    }

    @GetMapping("/api/players/{id}/rank")
    public LeaderboardEntryDto rank(@PathVariable Long id) {
        return LeaderboardEntryDto.of(leaderboardService.rankOf(id), leaderboardService.find(id));
    }
}
