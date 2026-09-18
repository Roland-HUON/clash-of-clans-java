package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.List;

import static com.rolandhuon.clashofclans.domain.common.ResourceType.DARK_ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.GOLD;

public enum BuildingType implements EntityType {

    LABORATORY("Laboratory", 1, ELIXIR, List.of(
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

    HEROHALL("Hero Hall", 1, DARK_ELIXIR, List.of(
            new BuildingStats(2000, 0),
            new BuildingStats(2400, 100),
            new BuildingStats(2800, 250),
            new BuildingStats(3200, 500),
            new BuildingStats(3600, 900),
            new BuildingStats(3800, 1500),
            new BuildingStats(4200, 2400),
            new BuildingStats(4600, 3600),
            new BuildingStats(5000, 5200),
            new BuildingStats(5400, 7200),
            new BuildingStats(5800, 9800),
            new BuildingStats(6000, 13000)
    )),

    MILITARY_CAMP("Military Camp", 4, GOLD,
            List.of(
                    new BuildingStats(250, 0),
                    new BuildingStats(290, 2000),
                    new BuildingStats(330, 8000),
                    new BuildingStats(370, 25000),
                    new BuildingStats(420, 60000)
            ),
            List.of(20, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 84, 88)),

    MONOLITH("Monolith", 1, DARK_ELIXIR, List.of(
            new BuildingStats(3000, 0),
            new BuildingStats(3400, 120),
            new BuildingStats(3800, 260)
    )),

    SPELL_FACTORY("Spell Factory", 1, ELIXIR, List.of(
            new BuildingStats(425, 0),
            new BuildingStats(470, 4000),
            new BuildingStats(520, 12000),
            new BuildingStats(570, 40000),
            new BuildingStats(620, 100000)
    )),

    PET_HOUSE("Pet House", 1, DARK_ELIXIR, List.of(
            new BuildingStats(1500, 0),
            new BuildingStats(1700, 400),
            new BuildingStats(1900, 800),
            new BuildingStats(2100, 1500)
    ));

    private final String label;
    private final int maxCount;
    private final ResourceType upgradeResource;
    private final List<BuildingStats> statsByLevel;
    private final List<Integer> housingCapacityByLevel;

    BuildingType(String label, int maxCount, ResourceType upgradeResource, List<BuildingStats> statsByLevel){
        this(label, maxCount, upgradeResource, statsByLevel, List.of());
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, List<Integer> housingCapacityByLevel){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException("Add levels pls.");
        if(!housingCapacityByLevel.isEmpty() && housingCapacityByLevel.size() != statsByLevel.size()){
            throw new IllegalArgumentException(label + ": capacity table and stats table must have the same size");
        }
        this.label = label;
        this.maxCount = maxCount;
        this.upgradeResource = upgradeResource;
        this.statsByLevel = List.copyOf(statsByLevel);
        this.housingCapacityByLevel = List.copyOf(housingCapacityByLevel);
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

    @Override
    public int upgradeCostFrom(int currentLevel){
        if(currentLevel >= maxLevel()) throw new IllegalStateException(label + " is already at max level (" + maxLevel() + ")");
        return statsAt(currentLevel + 1).upgradeCost();
    }

    @Override
    public ResourceType upgradeResource(){
        return upgradeResource;
    }

    public boolean storesTroops(){
        return !housingCapacityByLevel.isEmpty();
    }

    public int housingCapacityAt(int level){
        statsAt(level);
        return storesTroops() ? housingCapacityByLevel.get(level - 1) : 0;
    }

    public int maxCount(){
        return maxCount;
    }

    @Override
    public String label(){
        return label;
    }
}
