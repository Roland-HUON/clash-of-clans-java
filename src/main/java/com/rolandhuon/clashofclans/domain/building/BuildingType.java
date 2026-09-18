package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.AttackProfile;
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
            new BuildingStats(500, 250),
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
            new BuildingStats(2000, 50),
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
                    new BuildingStats(100, 1500),
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
            new BuildingStats(300, 250),
            new BuildingStats(360, 1000),
            new BuildingStats(420, 4000),
            new BuildingStats(500, 16000),
            new BuildingStats(600, 50000),
            new BuildingStats(660, 60000),
            new BuildingStats(730, 100000),
            new BuildingStats(800, 160000),
            new BuildingStats(880, 250000),
            new BuildingStats(960, 330000),
            new BuildingStats(1060, 500000),
            new BuildingStats(1160, 600000),
            new BuildingStats(1260, 660000),
            new BuildingStats(1380, 1000000),
            new BuildingStats(1500, 1200000),
            new BuildingStats(1620, 1300000),
            new BuildingStats(1740, 1500000),
            new BuildingStats(1870, 1800000),
            new BuildingStats(2000, 2000000),
            new BuildingStats(2150, 2600000),
            new BuildingStats(2250, 3000000)
    ),
            new Firepower(List.of(7, 10, 13, 17, 23, 30, 40, 48, 56, 64, 74, 85, 95, 100, 105, 110, 115, 125, 135, 150, 160), TargetScope.GROUND_ONLY)),

    ARCHER_TOWER("Archer Tower", 8, GOLD, List.of(
            new BuildingStats(380, 1000),
            new BuildingStats(420, 2000),
            new BuildingStats(460, 5000),
            new BuildingStats(500, 20000),
            new BuildingStats(540, 70000),
            new BuildingStats(580, 80000),
            new BuildingStats(630, 150000),
            new BuildingStats(690, 200000),
            new BuildingStats(750, 400000),
            new BuildingStats(810, 460000),
            new BuildingStats(890, 600000),
            new BuildingStats(970, 700000),
            new BuildingStats(1050, 1000000),
            new BuildingStats(1130, 1100000),
            new BuildingStats(1230, 1300000),
            new BuildingStats(1310, 1600000),
            new BuildingStats(1390, 1800000),
            new BuildingStats(1510, 2000000),
            new BuildingStats(1600, 2200000),
            new BuildingStats(1700, 3000000),
            new BuildingStats(1800, 4000000)
    ),
            new Firepower(List.of(11, 15, 19, 25, 30, 35, 42, 48, 56, 63, 70, 74, 78, 82, 85, 90, 100, 110, 120, 135, 145), TargetScope.ALL)),

    MORTAR("Mortar", 4, GOLD, List.of(
            new BuildingStats(400, 5000),
            new BuildingStats(450, 25000),
            new BuildingStats(500, 90000),
            new BuildingStats(550, 180000),
            new BuildingStats(600, 300000),
            new BuildingStats(650, 500000),
            new BuildingStats(700, 900000),
            new BuildingStats(800, 1200000),
            new BuildingStats(950, 1600000),
            new BuildingStats(1100, 1800000),
            new BuildingStats(1300, 2300000),
            new BuildingStats(1500, 2400000),
            new BuildingStats(1700, 2800000),
            new BuildingStats(1950, 4300000),
            new BuildingStats(2150, 5000000),
            new BuildingStats(2300, 7000000),
            new BuildingStats(2450, 13000000),
            new BuildingStats(2550, 21000000)
    ),
            new Firepower(List.of(4, 5, 6, 7, 9, 11, 15, 20, 25, 30, 35, 38, 42, 48, 54, 60, 66, 72), TargetScope.GROUND_ONLY, AttackProfile.splash(4.0, 1.5))),

    WIZARD_TOWER("Wizard Tower", 5, GOLD, List.of(
            new BuildingStats(620, 100000),
            new BuildingStats(650, 150000),
            new BuildingStats(680, 250000),
            new BuildingStats(730, 400000),
            new BuildingStats(840, 550000),
            new BuildingStats(960, 660000),
            new BuildingStats(1200, 1000000),
            new BuildingStats(1440, 1100000),
            new BuildingStats(1600, 1300000),
            new BuildingStats(1900, 2000000),
            new BuildingStats(2120, 2500000),
            new BuildingStats(2240, 2600000),
            new BuildingStats(2500, 3000000),
            new BuildingStats(2800, 4500000),
            new BuildingStats(3000, 5500000),
            new BuildingStats(3150, 8000000),
            new BuildingStats(3300, 14000000)
    ),
            new Firepower(List.of(11, 13, 16, 20, 24, 32, 40, 45, 50, 62, 70, 78, 84, 90, 95, 102, 110), TargetScope.ALL, AttackProfile.splash(3.0, 0.3))),

    AIR_DEFENSE("Air Defense", 4, GOLD, List.of(
            new BuildingStats(800, 220000),
            new BuildingStats(850, 900000),
            new BuildingStats(900, 210000),
            new BuildingStats(950, 500000),
            new BuildingStats(1000, 800000),
            new BuildingStats(1050, 1000000),
            new BuildingStats(1100, 1750000),
            new BuildingStats(1210, 2300000),
            new BuildingStats(1300, 3400000),
            new BuildingStats(1400, 5000000),
            new BuildingStats(1500, 5600000),
            new BuildingStats(1650, 6500000),
            new BuildingStats(1750, 8000000),
            new BuildingStats(1850, 9000000),
            new BuildingStats(1950, 15000000),
            new BuildingStats(2000, 26000000)
    ),
            new Firepower(List.of(80, 110, 140, 160, 190, 230, 280, 320, 360, 400, 440, 500, 540, 600, 650, 700), TargetScope.AIR_ONLY)),

    HIDDEN_TESLA("Hidden Tesla", 5, GOLD, List.of(
            new BuildingStats(600, 250000),
            new BuildingStats(630, 350000),
            new BuildingStats(660, 500000),
            new BuildingStats(690, 600000),
            new BuildingStats(730, 800000),
            new BuildingStats(770, 1200000),
            new BuildingStats(810, 1400000),
            new BuildingStats(850, 1650000),
            new BuildingStats(900, 2100000),
            new BuildingStats(980, 3000000),
            new BuildingStats(1100, 3100000),
            new BuildingStats(1200, 3700000),
            new BuildingStats(1350, 5100000),
            new BuildingStats(1450, 6500000),
            new BuildingStats(1550, 8200000),
            new BuildingStats(1650, 15000000),
            new BuildingStats(1750, 25000000)
    ),
            new Firepower(List.of(34, 40, 48, 55, 64, 75, 87, 99, 110, 120, 130, 140, 150, 160, 170, 180, 190), TargetScope.ALL)),

    RICOCHET_CANNON("Ricochet Cannon", 3, GOLD, List.of(
            new BuildingStats(5400, 12000000),
            new BuildingStats(5750, 13000000),
            new BuildingStats(6000, 17500000),
            new BuildingStats(6100, 26500000)
    ),
            new Firepower(List.of(360, 390, 405, 412), TargetScope.GROUND_ONLY, AttackProfile.splash(9.0, 0.2))),

    MULTI_ARCHER_TOWER("Multi-Archer Tower", 4, GOLD, List.of(
            new BuildingStats(5000, 12000000),
            new BuildingStats(5200, 13000000),
            new BuildingStats(5400, 17500000),
            new BuildingStats(5500, 27000000)
    ),
            new Firepower(List.of(300, 324, 348, 363), TargetScope.ALL)),

    MULTI_GEAR_TOWER("Multi-Gear Tower", 2, GOLD, List.of(
            new BuildingStats(4000, 1700000),
            new BuildingStats(4200, 1800000),
            new BuildingStats(4350, 2800000)
    ),
            new Firepower(List.of(350, 370, 390), TargetScope.GROUND_ONLY, AttackProfile.splash(4.0, 1.0))),

    GOLD_MINE("Gold Mine", 7, ELIXIR, List.of(
            new BuildingStats(75, 150),
            new BuildingStats(150, 300),
            new BuildingStats(300, 700),
            new BuildingStats(400, 1400),
            new BuildingStats(500, 3000),
            new BuildingStats(550, 7000),
            new BuildingStats(600, 14000),
            new BuildingStats(660, 28000),
            new BuildingStats(720, 56000),
            new BuildingStats(780, 75000),
            new BuildingStats(860, 85000),
            new BuildingStats(960, 170000),
            new BuildingStats(1080, 400000),
            new BuildingStats(1180, 800000),
            new BuildingStats(1280, 1200000),
            new BuildingStats(1350, 20000000),
            new BuildingStats(1400, 80000000)
    ),
            new Production(List.of(200, 400, 600, 800, 1000, 1300, 1600, 1900, 2200, 2800, 3500, 4200, 4900, 5600, 6300, 7000, 7560), ResourceType.GOLD)),

    ELIXIR_COLLECTOR("Elixir Collector", 7, GOLD, List.of(
            new BuildingStats(75, 150),
            new BuildingStats(150, 300),
            new BuildingStats(300, 700),
            new BuildingStats(400, 1400),
            new BuildingStats(500, 3000),
            new BuildingStats(550, 7000),
            new BuildingStats(600, 14000),
            new BuildingStats(660, 28000),
            new BuildingStats(720, 56000),
            new BuildingStats(780, 75000),
            new BuildingStats(860, 85000),
            new BuildingStats(960, 170000),
            new BuildingStats(1080, 400000),
            new BuildingStats(1180, 800000),
            new BuildingStats(1280, 1200000),
            new BuildingStats(1350, 20000000),
            new BuildingStats(1400, 80000000)
    ),
            new Production(List.of(200, 400, 600, 800, 1000, 1300, 1600, 1900, 2200, 2800, 3500, 4200, 4900, 5600, 6300, 7000, 7560), ResourceType.ELIXIR)),

    GOLD_STORAGE("Gold Storage", 4, ELIXIR, List.of(
            new BuildingStats(150, 300),
            new BuildingStats(300, 750),
            new BuildingStats(450, 1500),
            new BuildingStats(600, 3000),
            new BuildingStats(800, 6000),
            new BuildingStats(1000, 12000),
            new BuildingStats(1250, 25000),
            new BuildingStats(1500, 50000),
            new BuildingStats(1700, 100000),
            new BuildingStats(1900, 250000),
            new BuildingStats(2100, 500000),
            new BuildingStats(2500, 1000000),
            new BuildingStats(2900, 1800000),
            new BuildingStats(3300, 2800000),
            new BuildingStats(3700, 3000000),
            new BuildingStats(3900, 4000000),
            new BuildingStats(4050, 5500000),
            new BuildingStats(4200, 10000000),
            new BuildingStats(4300, 18000000)
    ),
            new Storage(List.of(1500, 3000, 6000, 12000, 25000, 45000, 100000, 225000, 450000, 850000, 1750000, 2000000, 3000000, 4000000, 4500000, 5000000, 5500000, 6000000, 6500000), ResourceType.GOLD)),

    ELIXIR_STORAGE("Elixir Storage", 4, GOLD, List.of(
            new BuildingStats(150, 300),
            new BuildingStats(300, 750),
            new BuildingStats(450, 1500),
            new BuildingStats(600, 3000),
            new BuildingStats(800, 6000),
            new BuildingStats(1000, 12000),
            new BuildingStats(1250, 25000),
            new BuildingStats(1500, 50000),
            new BuildingStats(1700, 100000),
            new BuildingStats(1900, 250000),
            new BuildingStats(2100, 500000),
            new BuildingStats(2500, 1000000),
            new BuildingStats(2900, 1800000),
            new BuildingStats(3300, 2800000),
            new BuildingStats(3700, 3000000),
            new BuildingStats(3900, 4000000),
            new BuildingStats(4050, 5500000),
            new BuildingStats(4200, 10000000),
            new BuildingStats(4300, 18000000)
    ),
            new Storage(List.of(1500, 3000, 6000, 12000, 25000, 45000, 100000, 225000, 450000, 850000, 1750000, 2000000, 3000000, 4000000, 4500000, 5000000, 5500000, 6000000, 6500000), ResourceType.ELIXIR)),

    DARK_ELIXIR_DRILL("Dark Elixir Drill", 3, ELIXIR, List.of(
            new BuildingStats(800, 180000),
            new BuildingStats(860, 270000),
            new BuildingStats(920, 540000),
            new BuildingStats(980, 900000),
            new BuildingStats(1060, 1200000),
            new BuildingStats(1160, 1800000),
            new BuildingStats(1280, 2100000),
            new BuildingStats(1380, 2400000),
            new BuildingStats(1480, 3700000),
            new BuildingStats(1550, 5300000),
            new BuildingStats(1600, 12000000)
    ),
            new Production(List.of(20, 30, 45, 60, 80, 100, 120, 140, 160, 180, 200), ResourceType.DARK_ELIXIR)),

    DARK_ELIXIR_STORAGE("Dark Elixir Storage", 1, ELIXIR, List.of(
            new BuildingStats(2000, 250000),
            new BuildingStats(2200, 500000),
            new BuildingStats(2400, 1000000),
            new BuildingStats(2600, 1500000),
            new BuildingStats(2900, 2000000),
            new BuildingStats(3200, 2400000),
            new BuildingStats(3500, 3800000),
            new BuildingStats(3800, 5400000),
            new BuildingStats(4100, 6600000),
            new BuildingStats(4300, 8000000),
            new BuildingStats(4500, 10000000),
            new BuildingStats(4700, 16000000),
            new BuildingStats(4800, 25000000)
    ),
            new Storage(List.of(10000, 17500, 40000, 75000, 140000, 180000, 220000, 280000, 330000, 360000, 390000, 420000, 450000), ResourceType.DARK_ELIXIR)),

    MONOLITH("Monolith", 1, DARK_ELIXIR, List.of(
            new BuildingStats(4747, 200000),
            new BuildingStats(5050, 250000),
            new BuildingStats(5353, 260000),
            new BuildingStats(5656, 300000),
            new BuildingStats(5959, 470000)
    ),
            new Firepower(List.of(150, 175, 193, 209, 225), TargetScope.GROUND_ONLY)),

    SPELL_FACTORY("Spell Factory", 1, ELIXIR, List.of(
            new BuildingStats(425, 150000),
            new BuildingStats(470, 300000),
            new BuildingStats(520, 600000),
            new BuildingStats(600, 1200000),
            new BuildingStats(720, 2000000),
            new BuildingStats(840, 3500000),
            new BuildingStats(960, 9000000),
            new BuildingStats(1080, 14000000),
            new BuildingStats(1150, 24000000)
    )),

    PET_HOUSE("Pet House", 1, DARK_ELIXIR, List.of(
            new BuildingStats(700, 3000000),
            new BuildingStats(800, 4000000),
            new BuildingStats(900, 5000000),
            new BuildingStats(1000, 6000000),
            new BuildingStats(1050, 7000000),
            new BuildingStats(1100, 8000000),
            new BuildingStats(1150, 9000000),
            new BuildingStats(1200, 10000000),
            new BuildingStats(1250, 11000000),
            new BuildingStats(1300, 12000000),
            new BuildingStats(1350, 16500000),
            new BuildingStats(1400, 25500000)
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
    private final AttackProfile attackProfile;
    private final TargetingMode targetingMode;
    private final List<Integer> storageByLevel;
    private final ResourceType storedResource;

    BuildingType(String label, int maxCount, ResourceType upgradeResource, List<BuildingStats> statsByLevel){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), List.of(), TargetScope.NONE,
                List.of(), null, AttackProfile.single(0), TargetingMode.FIRST_ALIVE, List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, HousingCapacity capacity){
        this(label, maxCount, upgradeResource, statsByLevel, capacity.byLevel(), List.of(), TargetScope.NONE,
                List.of(), null, AttackProfile.single(0), TargetingMode.FIRST_ALIVE, List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, Storage storage){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), List.of(), TargetScope.NONE,
                List.of(), null, AttackProfile.single(0), TargetingMode.FIRST_ALIVE, storage.capacityByLevel(), storage.resource());
        requireSameSize(label, "storage", storage.capacityByLevel(), statsByLevel);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, Production production){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), List.of(), TargetScope.NONE,
                production.perHourByLevel(), production.resource(), AttackProfile.single(0), TargetingMode.FIRST_ALIVE, List.of(), null);
        requireSameSize(label, "production", production.perHourByLevel(), statsByLevel);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource,
                 List<BuildingStats> statsByLevel, Firepower firepower){
        this(label, maxCount, upgradeResource, statsByLevel, List.of(), firepower.byLevel(), firepower.targets(),
                List.of(), null, firepower.profile(), firepower.targeting(), List.of(), null);
    }

    BuildingType(String label, int maxCount, ResourceType upgradeResource, List<BuildingStats> statsByLevel,
                 List<Integer> housingCapacityByLevel, List<Integer> damageByLevel, TargetScope targetScope,
                 List<Integer> productionByLevel, ResourceType producedResource,
                 AttackProfile attackProfile, TargetingMode targetingMode,
                 List<Integer> storageByLevel, ResourceType storedResource){
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
        this.attackProfile = attackProfile;
        this.targetingMode = targetingMode;
        this.storageByLevel = List.copyOf(storageByLevel);
        this.storedResource = storedResource;
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
            case GOLD_MINE, ELIXIR_COLLECTOR, DARK_ELIXIR_DRILL,
                 GOLD_STORAGE, ELIXIR_STORAGE, DARK_ELIXIR_STORAGE -> true;
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

    public int mineCapacityAt(int level){
        return productionPerHourAt(level) * Production.STORAGE_HOURS;
    }

    public AttackProfile attackProfile(){
        return attackProfile;
    }

    public TargetingMode targetingMode(){
        return targetingMode;
    }

    public boolean stores(){
        return !storageByLevel.isEmpty();
    }

    public ResourceType storedResource(){
        return storedResource;
    }

    public int storageCapacityAt(int level){
        statsAt(level);
        return stores() ? storageByLevel.get(level - 1) : 0;
    }

    public int buildCost(){
        return statsAt(1).upgradeCost();
    }

    public int costUpTo(int level){
        statsAt(level);
        int total = buildCost();
        for(int current = 1; current < level; current++){
            total += upgradeCostFrom(current);
        }
        return total;
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
