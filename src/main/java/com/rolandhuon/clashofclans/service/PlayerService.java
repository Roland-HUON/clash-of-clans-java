package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.dto.PlayerRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player create(PlayerRequest request) {
        Player player = new Player(
                request.name(),
                request.level(),
                request.gold(),
                request.elixir(),
                request.darkElixir());

        return playerRepository.save(player);
    }

    public List<Player> findByName(String name) {
        return playerRepository.findByName(name);
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public Player update(Long id, PlayerRequest request) {
        Player player = findById(id);

        player.setName(request.name());
        player.setLevel(request.level());
        player.setGold(request.gold());
        player.setElixir(request.elixir());
        player.setDarkElixir(request.darkElixir());

        return playerRepository.save(player);
    }

    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new PlayerNotFoundException(id);
        }
        playerRepository.deleteById(id);
    }
}
