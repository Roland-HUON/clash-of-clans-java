package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final PlayerRepository playerRepository;

    public LeaderboardService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> ranking(Integer limit) {
        int size = (limit == null) ? DEFAULT_LIMIT : limit;
        if (size < 1 || size > MAX_LIMIT) {
            throw new IllegalArgumentException("Limit must be between 1 and " + MAX_LIMIT);
        }
        return playerRepository.findAllByOrderByTrophiesDescNameAsc().stream()
                .limit(size)
                .toList();
    }

    public int rankOf(Long playerId) {
        List<Player> ranking = playerRepository.findAllByOrderByTrophiesDescNameAsc();

        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getId().equals(playerId)) {
                return i + 1;
            }
        }
        throw new PlayerNotFoundException(playerId);
    }

    public Player find(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
