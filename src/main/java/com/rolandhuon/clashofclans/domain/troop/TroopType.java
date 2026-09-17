package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import com.rolandhuon.clashofclans.domain.common.EntityType;

import java.util.List;

import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.FIRST_ALIVE;
import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.WEAKEST_FIRST;

public enum TroopType implements EntityType {
    BARBARIAN("Barbarian", 1, FIRST_ALIVE, AttackProfile.single(0.4), List.of(
            new TroopStats(45, 8),
            new TroopStats(54, 11),
            new TroopStats(65, 14),
            new TroopStats(85, 18),
            new TroopStats(105, 23),
            new TroopStats(125, 26),
            new TroopStats(160, 30),
            new TroopStats(205, 34),
            new TroopStats(230, 38),
            new TroopStats(250, 42),
            new TroopStats(270, 45),
            new TroopStats(290, 48),
            new TroopStats(310, 51)
    )),

    ARCHER("Archer", 1, WEAKEST_FIRST, AttackProfile.single(3.5), List.of(
            new TroopStats(20, 7),
            new TroopStats(23, 9),
            new TroopStats(28, 12),
            new TroopStats(33, 16),
            new TroopStats(40, 20),
            new TroopStats(44, 22),
            new TroopStats(48, 25),
            new TroopStats(52, 28),
            new TroopStats(56, 31),
            new TroopStats(60, 34),
            new TroopStats(64, 37),
            new TroopStats(68, 40),
            new TroopStats(72, 44),
            new TroopStats(76, 46)
    ));

    private final String label;
    private final int housingSpace;
    private final TargetingMode targetingMode;
    private final AttackProfile attackProfile;
    private final List<TroopStats> statsByLevel;

    TroopType(String label, int housingSpace, TargetingMode mode, AttackProfile kind, List<TroopStats> statsByLevel){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException("Add levels pls.");
        this.label = label;
        this.housingSpace = housingSpace;
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
    public String label(){
        return label;
    }

    public int housingSpace(){
        return housingSpace;
    }

    public AttackProfile attackProfile() {
        return attackProfile;
    }

    public TargetingMode targetingMode(){ return targetingMode; }
}
