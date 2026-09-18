package com.rolandhuon.clashofclans.domain.building;

public record BuildingStats(int hp, int upgradeCost) {

    public BuildingStats{
        if(hp <= 0) throw new IllegalArgumentException("No valid stats !");
        if(upgradeCost < 0) throw new IllegalArgumentException("Upgrade cost must be >= 0");
    }
}
