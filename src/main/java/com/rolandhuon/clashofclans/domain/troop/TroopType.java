package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.List;

import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.FIRST_ALIVE;
import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.WEAKEST_FIRST;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.ELIXIR;

public enum TroopType implements EntityType {

    BARBARIAN("Barbarian", 1, ELIXIR, FIRST_ALIVE, AttackProfile.single(0.4), List.of(
            new TroopStats(45, 8, 0),
            new TroopStats(54, 11, 200),
            new TroopStats(65, 14, 400),
            new TroopStats(85, 18, 800),
            new TroopStats(105, 23, 1500),
            new TroopStats(125, 26, 3000),
            new TroopStats(160, 30, 5000),
            new TroopStats(205, 34, 8000),
            new TroopStats(230, 38, 12000),
            new TroopStats(250, 42, 18000),
            new TroopStats(270, 45, 25000),
            new TroopStats(290, 48, 35000),
            new TroopStats(310, 51, 50000)
    )),

    ARCHER("Archer", 1, ELIXIR, WEAKEST_FIRST, AttackProfile.single(3.5), List.of(
            new TroopStats(20, 7, 0),
            new TroopStats(23, 9, 200),
            new TroopStats(28, 12, 400),
            new TroopStats(33, 16, 800),
            new TroopStats(40, 20, 1500),
            new TroopStats(44, 22, 3000),
            new TroopStats(48, 25, 5000),
            new TroopStats(52, 28, 8000),
            new TroopStats(56, 31, 12000),
            new TroopStats(60, 34, 18000),
            new TroopStats(64, 37, 25000),
            new TroopStats(68, 40, 35000),
            new TroopStats(72, 44, 50000),
            new TroopStats(76, 46, 70000)
    ));

    private final String label;
    private final int housingSpace;
    private final ResourceType upgradeResource;
    private final TargetingMode targetingMode;
    private final AttackProfile attackProfile;
    private final List<TroopStats> statsByLevel;

    TroopType(String label, int housingSpace, ResourceType upgradeResource, TargetingMode mode, AttackProfile kind, List<TroopStats> statsByLevel){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException("Add levels pls.");
        this.label = label;
        this.housingSpace = housingSpace;
        this.upgradeResource = upgradeResource;
        this.targetingMode = mode;
        this.attackProfile = kind;
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

    public TroopStats statsAt(int level){
        if(level < 1 || level > maxLevel()) throw new IllegalArgumentException("Level abnormal !");
        return statsByLevel.get(level - 1);
    }

    @Override
    public int upgradeCostFrom(int currentLevel){
        if(currentLevel >= maxLevel()) throw new IllegalStateException(label + " is already at max level (" + maxLevel() + ")");
        return statsAt(currentLevel + 1).upgradeCost();
    }

    @Override
    public String label(){
        return label;
    }

    @Override
    public ResourceType upgradeResource(){
        return upgradeResource;
    }

    public int housingSpace(){
        return housingSpace;
    }

    public AttackProfile attackProfile() {
        return attackProfile;
    }

    public TargetingMode targetingMode(){ return targetingMode; }
}
