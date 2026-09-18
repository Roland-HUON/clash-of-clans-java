package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.Movement;
import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.DEFENSE_FIRST;
import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.RESOURCE_FIRST;
import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.FIRST_ALIVE;
import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.WEAKEST_FIRST;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.DARK_ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.Movement.AIR;
import static com.rolandhuon.clashofclans.domain.common.Movement.GROUND;

public enum TroopType implements EntityType {

    BARBARIAN("Barbarian", 1, ELIXIR, FIRST_ALIVE, GROUND, AttackProfile.single(0.4), List.of(
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

    ARCHER("Archer", 1, ELIXIR, WEAKEST_FIRST, GROUND, AttackProfile.single(3.5), List.of(
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
    )),
    GIANT("Giant", 5, ELIXIR, DEFENSE_FIRST, GROUND, AttackProfile.single(1.0), List.of(
            new TroopStats(400, 12, 0),
            new TroopStats(500, 15, 40000),
            new TroopStats(600, 20, 150000),
            new TroopStats(700, 24, 400000),
            new TroopStats(900, 31, 800000),
            new TroopStats(1100, 43, 1500000),
            new TroopStats(1300, 55, 2300000),
            new TroopStats(1500, 62, 2600000),
            new TroopStats(1850, 70, 3400000),
            new TroopStats(2000, 78, 5000000),
            new TroopStats(2200, 86, 7500000),
            new TroopStats(2400, 94, 10000000),
            new TroopStats(2700, 104, 15000000),
            new TroopStats(3000, 114, 25000000)
    )),

    GOBLIN("Goblin", 1, ELIXIR, RESOURCE_FIRST, GROUND, AttackProfile.single(0.4), List.of(
            new TroopStats(25, 11, 0),
            new TroopStats(30, 14, 45000),
            new TroopStats(36, 19, 100000),
            new TroopStats(50, 24, 500000),
            new TroopStats(65, 32, 700000),
            new TroopStats(80, 42, 1600000),
            new TroopStats(105, 52, 2200000),
            new TroopStats(126, 62, 3700000),
            new TroopStats(146, 72, 8000000),
            new TroopStats(166, 82, 26000000)
    )),

    BALLOON("Balloon", 5, ELIXIR, DEFENSE_FIRST, AIR, AttackProfile.splash(0.8, 1.2), List.of(
            new TroopStats(150, 25, 0),
            new TroopStats(180, 32, 100000),
            new TroopStats(216, 48, 400000),
            new TroopStats(280, 72, 720000),
            new TroopStats(390, 108, 1300000),
            new TroopStats(545, 162, 2750000),
            new TroopStats(690, 198, 4400000),
            new TroopStats(840, 236, 5000000),
            new TroopStats(940, 256, 7000000),
            new TroopStats(1040, 276, 10000000),
            new TroopStats(1140, 290, 14000000),
            new TroopStats(1240, 304, 17500000),
            new TroopStats(1360, 326, 28000000)
    )),

    WIZARD("Wizard", 4, ELIXIR, FIRST_ALIVE, GROUND, AttackProfile.splash(3.0, 0.3), List.of(
            new TroopStats(75, 50, 0),
            new TroopStats(90, 70, 120000),
            new TroopStats(108, 90, 300000),
            new TroopStats(135, 125, 600000),
            new TroopStats(165, 170, 1200000),
            new TroopStats(180, 185, 2000000),
            new TroopStats(195, 200, 2500000),
            new TroopStats(210, 215, 3100000),
            new TroopStats(230, 230, 4000000),
            new TroopStats(250, 245, 5500000),
            new TroopStats(270, 260, 10000000),
            new TroopStats(290, 275, 11500000),
            new TroopStats(310, 290, 16000000),
            new TroopStats(330, 310, 27000000)
    )),

    HOG_RIDER("Hog Rider", 5, DARK_ELIXIR, DEFENSE_FIRST, GROUND, AttackProfile.single(0.8), List.of(
            new TroopStats(270, 60, 0),
            new TroopStats(312, 70, 2000),
            new TroopStats(370, 80, 3500),
            new TroopStats(430, 92, 5000),
            new TroopStats(500, 105, 10000),
            new TroopStats(590, 118, 18500),
            new TroopStats(700, 140, 35000),
            new TroopStats(810, 155, 47500),
            new TroopStats(890, 165, 50000),
            new TroopStats(970, 176, 85000),
            new TroopStats(1080, 187, 107500),
            new TroopStats(1230, 200, 125000),
            new TroopStats(1380, 213, 175000),
            new TroopStats(1500, 225, 240000),
            new TroopStats(1700, 250, 340000)
    )),

    HEALER("Healer", 14, ELIXIR, WEAKEST_FIRST, AIR, TroopRole.SUPPORT, AttackProfile.single(5.0), List.of(
            new TroopStats(500, 36, 0),
            new TroopStats(700, 48, 450000),
            new TroopStats(900, 60, 900000),
            new TroopStats(1200, 66, 2500000),
            new TroopStats(1500, 72, 4000000),
            new TroopStats(1600, 72, 6000000),
            new TroopStats(1700, 72, 9500000),
            new TroopStats(1800, 76, 11000000),
            new TroopStats(1900, 80, 13000000),
            new TroopStats(2000, 80, 17000000),
            new TroopStats(2100, 82, 28500000)
    )),

    DRAGON("Dragon", 20, ELIXIR, FIRST_ALIVE, AIR, AttackProfile.splash(3.0, 0.3), List.of(
            new TroopStats(1900, 140, 0),
            new TroopStats(2100, 160, 1000000),
            new TroopStats(2300, 180, 2000000),
            new TroopStats(2700, 210, 3000000),
            new TroopStats(3100, 240, 3800000),
            new TroopStats(3400, 270, 4900000),
            new TroopStats(3900, 310, 5000000),
            new TroopStats(4200, 330, 7500000),
            new TroopStats(4500, 350, 10500000),
            new TroopStats(4900, 370, 12000000),
            new TroopStats(5300, 390, 14000000),
            new TroopStats(5700, 410, 18500000),
            new TroopStats(6000, 430, 28500000)
    ));

    private final String label;
    public static final int MAX_ARMY_SIZE = 352;
    public static final int MAX_ARMY_ENTRIES = 20;

    private final int housingSpace;
    private final ResourceType upgradeResource;
    private final TargetingMode targetingMode;
    private final Movement movement;
    private final AttackProfile attackProfile;
    private final TroopRole role;
    private final List<TroopStats> statsByLevel;

    TroopType(String label, int housingSpace, ResourceType upgradeResource, TargetingMode mode,
              Movement movement, AttackProfile kind, List<TroopStats> statsByLevel){
        this(label, housingSpace, upgradeResource, mode, movement, TroopRole.ATTACKER, kind, statsByLevel);
    }

    TroopType(String label, int housingSpace, ResourceType upgradeResource, TargetingMode mode,
              Movement movement, TroopRole role, AttackProfile kind, List<TroopStats> statsByLevel){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException(label + " must declare at least one level.");
        this.label = label;
        this.housingSpace = housingSpace;
        this.upgradeResource = upgradeResource;
        this.targetingMode = mode;
        this.movement = movement;
        this.role = role;
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
        if(level < 1 || level > maxLevel()) throw new IllegalArgumentException(
                label + ": level " + level + " is out of bounds [1.." + maxLevel() + "].");
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

    public TroopRole role(){ return role; }

    public Movement movement(){ return movement; }

    public static TroopType from(String code){
        if(code == null || code.isBlank()) throw new IllegalArgumentException("A troop type is required.");
        try {
            return valueOf(code.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unknown) {
            throw new IllegalArgumentException("Unknown troop type '" + code + "'. Known values: "
                    + Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", ")));
        }
    }
}
