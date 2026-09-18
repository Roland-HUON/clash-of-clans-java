package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.EntityType;
import com.rolandhuon.clashofclans.domain.common.Movement;
import com.rolandhuon.clashofclans.domain.common.TargetScope;
import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.rolandhuon.clashofclans.domain.common.ResourceType.DARK_ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.ELIXIR;
import static com.rolandhuon.clashofclans.domain.common.ResourceType.GOLD;

public enum BuildingType implements EntityType {

    LABORATORY("Laboratory", 1, ELIXIR, List.of(
            new BuildingStats(500,25000),
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
            new BuildingStats(2000,1000),
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

    MILITARY_CAMP("Military Camp", 4, ELIXIR,
            List.of(
                    new BuildingStats(100,10000),
                    new BuildingStats(120, 2500),
                    new BuildingStats(140, 10000),
                    new BuildingStats(160, 100000),
                    new BuildingStats(180, 250000),
                    new BuildingStats(200, 750000),
                    new BuildingStats(250, 1500000),
                    new BuildingStats(300, 2200000),
                    new BuildingStats(400, 3500000),
                    new BuildingStats(500, 4500000),
                    new BuildingStats(600, 7000000),
                    new BuildingStats(700, 11000000),
                    new BuildingStats(800, 16000000),
                    new BuildingStats(950, 19000000)
            ),
            new HousingCapacity(List.of(20, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 84, 88))),

    CANNON("Cannon", 7, GOLD, List.of(
            new BuildingStats(420,250),
            new BuildingStats(470, 1000),
            new BuildingStats(520, 4000),
            new BuildingStats(570, 16000),
            new BuildingStats(620, 50000),
            new BuildingStats(670, 100000),
            new BuildingStats(730, 200000),
            new BuildingStats(800, 400000)
    ),
            new Firepower(List.of(7, 9, 11, 15, 19, 25, 31, 40), TargetScope.GROUND_ONLY)),

    ARCHER_TOWER("Archer Tower", 8, GOLD, List.of(
            new BuildingStats(380,1000),
            new BuildingStats(420, 2000),
            new BuildingStats(460, 5000),
            new BuildingStats(500, 20000),
            new BuildingStats(540, 60000),
            new BuildingStats(580, 120000),
            new BuildingStats(630, 250000),
            new BuildingStats(690, 500000)
    ),
            new Firepower(List.of(11, 15, 19, 25, 30, 35, 41, 48), TargetScope.ALL)),

    MORTAR("Mortar", 4, GOLD, List.of(
            new BuildingStats(400,8000),
            new BuildingStats(450, 8000),
            new BuildingStats(500, 32000),
            new BuildingStats(550, 120000),
            new BuildingStats(600, 400000),
            new BuildingStats(650, 800000)
    ),
            new Firepower(List.of(4, 5, 6, 7, 9, 11), TargetScope.GROUND_ONLY)),

    WIZARD_TOWER("Wizard Tower", 5, GOLD, List.of(
            new BuildingStats(620,180000),
            new BuildingStats(660, 180000),
            new BuildingStats(700, 360000),
            new BuildingStats(740, 720000),
            new BuildingStats(810, 1200000)
    ),
            new Firepower(List.of(11, 13, 16, 20, 24), TargetScope.ALL)),

    AIR_DEFENSE("Air Defense", 4, GOLD, List.of(
            new BuildingStats(800,22500),
            new BuildingStats(850, 22500),
            new BuildingStats(900, 90000),
            new BuildingStats(950, 270000),
            new BuildingStats(1000, 700000)
    ),
            new Firepower(List.of(80, 110, 140, 160, 190), TargetScope.AIR_ONLY)),

    HIDDEN_TESLA("Hidden Tesla", 5, GOLD, List.of(
            new BuildingStats(600,100000),
            new BuildingStats(630, 100000),
            new BuildingStats(660, 200000),
            new BuildingStats(690, 400000),
            new BuildingStats(730, 800000)
    ),
            new Firepower(List.of(33, 41, 49, 57, 65), TargetScope.ALL)),

    RICOCHET_CANNON("Ricochet Cannon", 3, GOLD, List.of(
            new BuildingStats(1600,1000000),
            new BuildingStats(1750, 900000),
            new BuildingStats(1900, 1600000)
    ),
            new Firepower(List.of(85, 95, 105), TargetScope.GROUND_ONLY)),

    MULTI_ARCHER_TOWER("Multi-Archer Tower", 4, GOLD, List.of(
            new BuildingStats(1380,1000000),
            new BuildingStats(1500, 1000000),
            new BuildingStats(1620, 1800000)
    ),
            new Firepower(List.of(70, 80, 90), TargetScope.ALL)),

    MULTI_GEAR_TOWER("Multi-Gear Tower", 2, GOLD, List.of(
            new BuildingStats(1460,1000000),
            new BuildingStats(1580, 1200000),
            new BuildingStats(1700, 2000000)
    ),
            new Firepower(List.of(60, 70, 80), TargetScope.GROUND_ONLY)),

    GOLD_MINE("Gold Mine", 7, ELIXIR, List.of(
            new BuildingStats(400,150),
            new BuildingStats(440, 1500),
            new BuildingStats(480, 6000),
            new BuildingStats(520, 25000),
            new BuildingStats(560, 100000),
            new BuildingStats(600, 300000)
    ),
            new Production(List.of(200, 400, 600, 900, 1300, 1800), ResourceType.GOLD)),

    ELIXIR_COLLECTOR("Elixir Collector", 7, GOLD, List.of(
            new BuildingStats(400,150),
            new BuildingStats(440, 1500),
            new BuildingStats(480, 6000),
            new BuildingStats(520, 25000),
            new BuildingStats(560, 100000),
            new BuildingStats(600, 300000)
    ),
            new Production(List.of(200, 400, 600, 900, 1300, 1800), ResourceType.ELIXIR)),

    GOLD_STORAGE("Gold Storage", 4, ELIXIR, List.of(
            new BuildingStats(600,300),
            new BuildingStats(800, 20000),
            new BuildingStats(1000, 90000),
            new BuildingStats(1200, 300000),
            new BuildingStats(1400, 800000)
    )),

    ELIXIR_STORAGE("Elixir Storage", 4, GOLD, List.of(
            new BuildingStats(600,300),
            new BuildingStats(800, 20000),
            new BuildingStats(1000, 90000),
            new BuildingStats(1200, 300000),
            new BuildingStats(1400, 800000)
    )),

    MONOLITH("Monolith", 1, DARK_ELIXIR, List.of(
            new BuildingStats(3000,120),
            new BuildingStats(3400, 120),
            new BuildingStats(3800, 260)
    ),
            new Firepower(List.of(200, 230, 260), TargetScope.GROUND_ONLY)),

    SPELL_FACTORY("Spell Factory", 1, ELIXIR, List.of(
            new BuildingStats(425,20000),
            new BuildingStats(470, 4000),
            new BuildingStats(520, 12000),
            new BuildingStats(570, 40000),
            new BuildingStats(620, 100000)
    )),

    PET_HOUSE("Pet House", 1, DARK_ELIXIR, List.of(
            new BuildingStats(1500,800),
            new BuildingStats(1700, 400),
            new BuildingStats(1900, 800),
            new BuildingStats(2100, 1500)
    ));

    private final String label;
    private final int maxCount;
    private final ResourceType upgradeResource;
    private final List<BuildingStats> statsByLevel;
    private final List<Integer> housingCapacityByLevel;
    private final List<Integer> damageByLevel;
    private final TargetScope targetScope;
    private final List<Integer> productionByLevel;
    private final ResourceType producedResource;

    BuildingType(String label, int maxCount, ResourceType upgradeResource, List<BuildingStats> statsByLevel){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), List.of(), TargetScope.NONE, List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, HousingCapacity capacity){
        this(label, maxCount, upgradeResource, statsByLevel, capacity.byLevel(), List.of(), TargetScope.NONE, List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, Production production){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), List.of(), TargetScope.NONE,
                production.perHourByLevel(), production.resource());
        requireSameSize(label, "production", production.perHourByLevel(), statsByLevel);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, Firepower firepower){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), firepower.byLevel(), firepower.targets(), List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource, List<BuildingStats> statsByLevel,
                 List<Integer> housingCapacityByLevel, List<Integer> damageByLevel, TargetScope targetScope,
                 List<Integer> productionByLevel, ResourceType producedResource){
        if(statsByLevel.isEmpty()) throw new IllegalArgumentException(label + " must declare at least one level.");
        if(damageByLevel.isEmpty() != (targetScope == TargetScope.NONE)){
            throw new IllegalArgumentException(label + ": a building deals damage exactly when it has something to shoot at");
        }
        requireSameSize(label, "capacity", housingCapacityByLevel, statsByLevel);
        requireSameSize(label, "damage", damageByLevel, statsByLevel);
        this.label = label;
        this.maxCount = maxCount;
        this.upgradeResource = upgradeResource;
        this.statsByLevel = List.copyOf(statsByLevel);
        this.housingCapacityByLevel = List.copyOf(housingCapacityByLevel);
        this.damageByLevel = List.copyOf(damageByLevel);
        this.targetScope = targetScope;
        this.productionByLevel = List.copyOf(productionByLevel);
        this.producedResource = producedResource;
    }

    private static void requireSameSize(String label, String what, List<Integer> table, List<BuildingStats> stats){
        if(!table.isEmpty() && table.size() != stats.size()){
            throw new IllegalArgumentException(label + ": " + what + " table and stats table must have the same size");
        }
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

    public boolean isResourceBuilding(){
        return switch (this) {
            case GOLD_MINE, ELIXIR_COLLECTOR, GOLD_STORAGE, ELIXIR_STORAGE -> true;
            default -> false;
        };
    }

    public boolean isDefensive(){
        return !damageByLevel.isEmpty();
    }

    public TargetScope targetScope(){
        return targetScope;
    }

    public boolean canTarget(Movement movement){
        return targetScope.covers(movement);
    }

    public boolean produces(){
        return !productionByLevel.isEmpty();
    }

    public ResourceType producedResource(){
        return producedResource;
    }

    public int productionPerHourAt(int level){
        statsAt(level);
        return produces() ? productionByLevel.get(level - 1) : 0;
    }

    public int storageCapacityAt(int level){
        return productionPerHourAt(level) * Production.STORAGE_HOURS;
    }

    public int buildCost(){
        return statsAt(1).upgradeCost();
    }

    public int damageAt(int level){
        statsAt(level);
        return isDefensive() ? damageByLevel.get(level - 1) : 0;
    }

    public int maxCount(){
        return maxCount;
    }

    @Override
    public String label(){
        return label;
    }

    public static BuildingType from(String code){
        if(code == null || code.isBlank()) throw new IllegalArgumentException("A building type is required.");
        try {
            return valueOf(code.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unknown) {
            throw new IllegalArgumentException("Unknown building type '" + code + "'. Known values: "
                    + Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", ")));
        }
    }
}
