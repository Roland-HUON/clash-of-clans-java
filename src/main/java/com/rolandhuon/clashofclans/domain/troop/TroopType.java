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
            new TroopStats(300, 11, 0),
            new TroopStats(360, 14, 2000),
            new TroopStats(430, 17, 5000),
            new TroopStats(520, 21, 12000),
            new TroopStats(600, 24, 25000),
            new TroopStats(690, 28, 50000)
    )),

    GOBLIN("Goblin", 1, ELIXIR, RESOURCE_FIRST, GROUND, AttackProfile.single(0.4), List.of(
            new TroopStats(25, 11, 0),
            new TroopStats(30, 14, 1500),
            new TroopStats(36, 19, 4000),
            new TroopStats(50, 24, 10000),
            new TroopStats(65, 32, 20000),
            new TroopStats(80, 42, 40000)
    )),

    BALLOON("Balloon", 5, ELIXIR, DEFENSE_FIRST, AIR, AttackProfile.splash(0.8, 1.2), List.of(
            new TroopStats(150, 25, 0),
            new TroopStats(180, 32, 3000),
            new TroopStats(216, 48, 8000),
            new TroopStats(280, 72, 18000),
            new TroopStats(390, 108, 35000),
            new TroopStats(545, 162, 70000)
    )),

    WIZARD("Wizard", 4, ELIXIR, FIRST_ALIVE, GROUND, AttackProfile.splash(3.0, 0.3), List.of(
            new TroopStats(75, 50, 0),
            new TroopStats(90, 70, 4000),
            new TroopStats(108, 90, 10000),
            new TroopStats(130, 125, 22000),
            new TroopStats(156, 170, 45000),
            new TroopStats(190, 185, 90000)
    )),

    HOG_RIDER("Hog Rider", 5, DARK_ELIXIR, DEFENSE_FIRST, GROUND, AttackProfile.single(0.8), List.of(
            new TroopStats(270, 60, 0),
            new TroopStats(312, 70, 20),
            new TroopStats(360, 80, 40),
            new TroopStats(400, 91, 80),
            new TroopStats(450, 102, 150),
            new TroopStats(500, 115, 250)
    )),

    HEALER("Healer", 14, ELIXIR, WEAKEST_FIRST, AIR, TroopRole.SUPPORT, AttackProfile.single(5.0), List.of(
            new TroopStats(500, 36, 0),
            new TroopStats(600, 42, 8000),
            new TroopStats(690, 48, 20000),
            new TroopStats(790, 54, 45000),
            new TroopStats(880, 60, 90000)
    )),

    DRAGON("Dragon", 20, ELIXIR, FIRST_ALIVE, AIR, AttackProfile.splash(3.0, 0.3), List.of(
            new TroopStats(1900, 140, 0),
            new TroopStats(2100, 160, 120000),
            new TroopStats(2300, 180, 220000),
            new TroopStats(2700, 210, 400000),
            new TroopStats(3100, 240, 700000),
            new TroopStats(3400, 270, 1200000)
    ));

    private final String label;
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
