package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.building.MilitaryCamp;
import com.rolandhuon.clashofclans.domain.building.Monolith;
import com.rolandhuon.clashofclans.domain.building.PetHouse;
import com.rolandhuon.clashofclans.domain.building.SpellFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuildingFactory {
    public Building create(BuildingType type, int level){
        return switch (type){
            case LABORATORY -> new Laboratory(level);
            case HEROHALL -> new HeroHall(level);
            case MILITARY_CAMP -> new MilitaryCamp(level);
            case MONOLITH -> new Monolith(level);
            case SPELL_FACTORY -> new SpellFactory(level);
            case PET_HOUSE -> new PetHouse(level);
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
