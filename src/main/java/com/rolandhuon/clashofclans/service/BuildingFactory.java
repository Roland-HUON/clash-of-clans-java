package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingFactory {
    public Building create(BuildingType type, int level){
        return switch (type){
            case LABORATORY -> new Laboratory(level);
            case HEROHALL -> new HeroHall(level);
        };
    }

    public List<Building> create(BuildingType type, int level, int count) {
        if(count <= 0) throw new IllegalArgumentException("Add more of this building pls.");
        List<Building> buildings = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Building b = create(type, level);
            buildings.add(b);
        }
        return buildings;
    }
}
