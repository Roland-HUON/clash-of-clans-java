package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.dto.BuildingTypeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/building-types")
public class BuildingTypeController {
    @GetMapping
    public List<BuildingTypeDto> all(){
        return Arrays.stream(BuildingType.values())
                .map(t -> BuildingTypeDto.from(t))
                .toList();
    }

    @GetMapping("/{id}")
    public BuildingTypeDto find(@PathVariable String id){
        try{
            return BuildingTypeDto.from(BuildingType.valueOf(id.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Building " + id + " not exist.");
        }
    }
}
