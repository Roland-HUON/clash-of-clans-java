package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.EntityType;

import java.util.List;

public enum BuildingType implements EntityType {
    LABORATORY("Laboratory", 1, List.of(
            new BuildingStats(500),
            new BuildingStats(550),
            new BuildingStats(600),
            new BuildingStats(650),
            new BuildingStats(700),
            new BuildingStats(750),
            new BuildingStats(830),
            new BuildingStats(950),
            new BuildingStats(1070),
            new BuildingStats(1140),
            new BuildingStats(1210),
            new BuildingStats(1280),
            new BuildingStats(1350),
            new BuildingStats(1400),
            new BuildingStats(1450)
    )),

    HEROHALL("Hero Hall", 1, List.of(
            new BuildingStats(2000),
            new BuildingStats(2400),
            new BuildingStats(2800),
            new BuildingStats(3200),
            new BuildingStats(3600),
            new BuildingStats(3800),
            new BuildingStats(4200),
            new BuildingStats(4600),
            new BuildingStats(5000),
            new BuildingStats(5400),
            new BuildingStats(5800),
            new BuildingStats(6000)
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

    public int maxCount(){
        return maxCount;
    }

    @Override
    public String label(){
        return label;
    }
}
