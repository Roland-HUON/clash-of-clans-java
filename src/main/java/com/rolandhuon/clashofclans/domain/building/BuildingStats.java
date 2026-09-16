package com.rolandhuon.clashofclans.domain.building;

public record BuildingStats(int hp) {
    public BuildingStats{
        if(hp <= 0) throw new IllegalArgumentException("No valid stats !");
    }
}
