package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.dto.PlayerDto;
import com.rolandhuon.clashofclans.dto.PlayerRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerDto create(@RequestBody PlayerRequest request) {
        return PlayerDto.from(playerService.create(request));
    }

    @GetMapping
    public List<PlayerDto> all(@RequestParam(required = false) String name) {
        List<Player> players = (name == null)
                ? playerService.findAll()
                : playerService.findByName(name);

        return players.stream()
                .map(PlayerDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PlayerDto find(@PathVariable Long id) {
        return PlayerDto.from(playerService.findById(id));
    }

    @PutMapping("/{id}")
    public PlayerDto update(@PathVariable Long id, @RequestBody PlayerRequest request) {
        return PlayerDto.from(playerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
