package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.EntityType;

import java.util.List;

public enum BuildingType implements EntityType {

    LABORATORY("Laboratory", 1, List.of(
            new BuildingStats(500, 0),
            new BuildingStats(550, 500),
            new BuildingStats(600, 1500),
            new BuildingStats(650, 3000),
            new BuildingStats(700, 6000),
            new BuildingStats(750, 12000),
            new BuildingStats(830, 25000),
            new BuildingStats(950, 50000),
            new BuildingStats(1070, 90000),
            new BuildingStats(1140, 150000),
            new BuildingStats(1210, 250000),
            new BuildingStats(1280, 400000),
            new BuildingStats(1350, 600000),
            new BuildingStats(1400, 900000),
            new BuildingStats(1450, 1200000)
    )),

    HEROHALL("Hero Hall", 1, List.of(
            new BuildingStats(2000, 0),
            new BuildingStats(2400, 1000),
            new BuildingStats(2800, 3000),
            new BuildingStats(3200, 8000),
            new BuildingStats(3600, 20000),
            new BuildingStats(3800, 45000),
            new BuildingStats(4200, 90000),
            new BuildingStats(4600, 160000),
            new BuildingStats(5000, 280000),
            new BuildingStats(5400, 450000),
            new BuildingStats(5800, 700000),
            new BuildingStats(6000, 1000000)
    ));

    private final String label;
    private final int maxCount;
    private final List<BuildingStats> statsByLevel;

    BuildingType(String label, int maxCount, List<BuildingStats> statsByLevel){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException("Add levels pls.");
        this.label = label;
        this.maxCount = maxCount;
        this.statsByLevel = List.copyOf(statsByLevel);
    }

    @Override
    public int maxLevel(){
        return statsByLevel.size();
    }

    @Override
    public int hpAt(int level) {
        return statsAt(level).hp();
    }

    public BuildingStats statsAt(int level){
        if(level < 1 || level > maxLevel()) throw new IllegalArgumentException("Level abnormal !");
        return statsByLevel.get(level - 1);
    }

    public int upgradeCostFrom(int currentLevel){
        if(currentLevel >= maxLevel()) throw new IllegalStateException(label + " is already at max level (" + maxLevel() + ")");
        return statsAt(currentLevel + 1).goldCost();
    }

    public int maxCount(){
        return maxCount;
    }

    @Override
    public String label(){
        return label;
    }
}
