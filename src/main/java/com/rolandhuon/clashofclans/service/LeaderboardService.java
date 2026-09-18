package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaderboardService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final PlayerRepository playerRepository;

    public LeaderboardService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<Player> ranking(Integer limit) {
        int size = (limit == null) ? DEFAULT_LIMIT : limit;
        if (size < 1 || size > MAX_LIMIT) {
            throw new IllegalArgumentException("Limit must be between 1 and " + MAX_LIMIT);
        }
        return playerRepository.findAllByOrderByTrophiesDescNameAsc().stream()
                .limit(size)
                .toList();
    }

    @Transactional(readOnly = true)
    public int rankOf(Long playerId) {
        return rankOf(find(playerId));
    }

    @Transactional(readOnly = true)
    public int rankOf(Player player) {
        return (int) playerRepository.countAhead(player.getTrophies(), player.getName()) + 1;
    }

    @Transactional(readOnly = true)
    public Player find(Long playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
