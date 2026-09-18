package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.building.MilitaryCamp;
import com.rolandhuon.clashofclans.domain.building.DefensiveBuilding;
import com.rolandhuon.clashofclans.domain.building.PetHouse;
import com.rolandhuon.clashofclans.domain.building.ResourceBuilding;
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
            case CANNON, ARCHER_TOWER, MORTAR, WIZARD_TOWER, AIR_DEFENSE, HIDDEN_TESLA,
                 RICOCHET_CANNON, MULTI_ARCHER_TOWER, MULTI_GEAR_TOWER, MONOLITH -> new DefensiveBuilding(type, level);
            case SPELL_FACTORY -> new SpellFactory(level);
            case GOLD_MINE, ELIXIR_COLLECTOR, GOLD_STORAGE, ELIXIR_STORAGE -> new ResourceBuilding(type, level);
            case PET_HOUSE -> new PetHouse(level);
        };
    }

    public List<Building> create(BuildingType type, int level, int count) {
        if(count <= 0) throw new IllegalArgumentException("Count must be at least 1.");
        List<Building> buildings = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Building b = create(type, level);
            buildings.add(b);
        }
        return buildings;
    }
}
