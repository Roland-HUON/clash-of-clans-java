package com.rolandhuon.clashofclans.domain.village;

import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.common.Damageable;

import java.util.ArrayList;
import java.util.List;

public class Village {
    private final List<Building> buildings = new ArrayList<>();

    public void addBuilding(Building building){
        long buildingHere = buildings.stream()
                .filter(x -> x.getType() == building.getType())
                .count();
        if( buildingHere >= building.getType().maxCount()) throw new IllegalStateException("Invalid number of the building : " + building.getType().name());
        buildings.add(building);
    }

    public List<Damageable> aliveTargets(){
        return List.copyOf(buildings.stream()
                .filter(Building::isAlive)
                .toList());
    }

    public boolean isDestroyed(){
        return buildings.stream().noneMatch(Building::isAlive);
    }

    public int destructionPercentage(){
        if(buildings.isEmpty()) return 0;
        long destruction = buildings.stream()
                .filter(buildings -> !buildings.isAlive())
                .count();
        return (int) (destruction *100 / buildings.size());
    }
}
