package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.model.Player;

public record LeaderboardEntryDto(int rank, Long playerId, String name, int level, int trophies) {

    public static LeaderboardEntryDto of(int rank, Player player) {
        return new LeaderboardEntryDto(rank, player.getId(), player.getName(),
                player.getLevel(), player.getTrophies());
    }
}
