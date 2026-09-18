package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.dto.TroopTypeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Troop types", description = "The catalogue of troops: read-only game data, identical for every player.")
@RestController
@RequestMapping("/api/troop-types")
public class TroopTypeController {
    @Operation(summary = "List troop types",
            description = "Returns the nine troops with their housing space, maximum level, attack profile, whether they walk or fly, and whether they attack or support.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping
    public List<TroopTypeDto> all(){
        return Arrays.stream(TroopType.values())
                .map(t -> TroopTypeDto.from(t))
                .toList();
    }

    @Operation(summary = "Read one troop type",
            description = "Returns a single troop type by its code, for example BARBARIAN or DRAGON. The case does not matter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No troop type carries that code."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/{id}")
    public TroopTypeDto find(@PathVariable String id){
        try{
            return TroopTypeDto.from(TroopType.from(id));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Troop type", id);
        }
    }
}
