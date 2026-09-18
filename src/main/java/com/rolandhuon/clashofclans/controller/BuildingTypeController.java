package com.rolandhuon.clashofclans.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.rolandhuon.clashofclans.service.NotFoundException;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.dto.BuildingTypeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Building types", description = "The catalogue of buildings: read-only game data, identical for every player.")
@RestController
@RequestMapping("/api/building-types")
public class BuildingTypeController {
        @Operation(summary = "List building types",
            description = "Returns the twenty-one buildings with their maximum count and level, their build cost and currency, whether they are a defence, a resource building or a camp, and what a defence is allowed to shoot at.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping
    public List<BuildingTypeDto> all(){
        return Arrays.stream(BuildingType.values())
                .map(t -> BuildingTypeDto.from(t))
                .toList();
    }

        @Operation(summary = "Read one building type",
            description = "Returns a single building type by its code, for example CANNON or AIR_DEFENSE. The case does not matter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The request succeeded."),
            @ApiResponse(responseCode = "404", description = "No building type carries that code."),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded: more than 60 requests in a minute from this client.")
    })
    @GetMapping("/{id}")
    public BuildingTypeDto find(@PathVariable String id){
        try{
            return BuildingTypeDto.from(BuildingType.from(id));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Building type", id);
        }
    }
}
