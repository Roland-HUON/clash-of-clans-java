package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.dto.TroopTypeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/troop-types")
public class TroopTypeController {
    @GetMapping
    public List<TroopTypeDto> all(){
        return Arrays.stream(TroopType.values())
                .map(t -> TroopTypeDto.from(t))
                .toList();
    }

    @GetMapping("/{id}")
    public TroopTypeDto find(@PathVariable String id){
        try{
            return TroopTypeDto.from(TroopType.valueOf(id.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Troop " + id + " not exist.");
        }
    }
}
