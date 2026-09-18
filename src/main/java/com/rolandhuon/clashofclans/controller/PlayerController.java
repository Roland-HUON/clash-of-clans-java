package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.rolandhuon.clashofclans.dto.PlayerDto;
import com.rolandhuon.clashofclans.dto.PlayerRequest;
import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Players", description = "Chiefs: their account, their resources and their trophies.")
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

        @Operation(summary = "Create a player",
            description = "Registers a new chief with a starting purse. The name must not be blank and every amount must be zero or more.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The player was created and is returned with its generated id."),
            @ApiResponse(responseCode = "400", description = "The body is malformed, or a field breaks its validation rule."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerDto create(@Valid @RequestBody PlayerRequest request) {
        return PlayerDto.from(playerService.create(request));
    }

        @Operation(summary = "List players",
            description = "Returns every player ordered by id. Pass name to filter on an exact name instead.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping
    public List<PlayerDto> all(@RequestParam(required = false) String name) {
        List<Player> players = (name == null)
                ? playerService.findAll()
                : playerService.findByName(name);

        return players.stream()
                .map(PlayerDto::from)
                .toList();
    }

        @Operation(summary = "Read one player",
            description = "Returns a single chief with the balance of the three currencies and the trophy count.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/{id}")
    public PlayerDto find(@PathVariable Long id) {
        return PlayerDto.from(playerService.findById(id));
    }

        @Operation(summary = "Replace a player",
            description = "Overwrites the name, level and the three balances. The villages and troops are left untouched.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "400", description = "The body is malformed, or a field breaks its validation rule."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @PutMapping("/{id}")
    public PlayerDto update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        return PlayerDto.from(playerService.update(id, request));
    }

        @Operation(summary = "Delete a player",
            description = "Removes the chief along with the villages, buildings and troops that belong to them.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The player was deleted; nothing is returned."),
            @ApiResponse(responseCode = "404", description = "No player carries that id."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
