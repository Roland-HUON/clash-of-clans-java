package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.Movement;
import com.rolandhuon.clashofclans.domain.common.ResourceType;
import com.rolandhuon.clashofclans.domain.common.TargetScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuildingTypeTest {

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("Hit points and upgrade cost never decrease when the level goes up.")
    void statsNeverDecreaseWithLevel(BuildingType type) {
        for (int level = 2; level <= type.maxLevel(); level++) {
            assertThat(type.hpAt(level))
                    .as("%s level %d: hit points", type.label(), level)
                    .isGreaterThanOrEqualTo(type.hpAt(level - 1));

            assertThat(type.upgradeCostFrom(level - 1))
                    .as("%s level %d: upgrade cost", type.label(), level)
                    .isPositive();
        }
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("A defensive building deals damage at every level, the others deal none.")
    void onlyDefensiveTypesDealDamage(BuildingType type) {
        for (int level = 1; level <= type.maxLevel(); level++) {
            if (type.isDefensive()) {
                assertThat(type.damageAt(level))
                        .as("%s level %d: damage", type.label(), level)
                        .isPositive();
            } else {
                assertThat(type.damageAt(level))
                        .as("%s level %d must not deal damage", type.label(), level)
                        .isZero();
            }
        }
    }

    @Test
    @DisplayName("Defences are paid in gold, except the Monolith which is the only dark elixir one.")
    void theMonolithIsTheOnlyDarkElixirDefence() {
        List<BuildingType> darkElixirDefences = Arrays.stream(BuildingType.values())
                .filter(BuildingType::isDefensive)
                .filter(type -> type.upgradeResource() == ResourceType.DARK_ELIXIR)
                .toList();

        assertThat(darkElixirDefences).containsExactly(BuildingType.MONOLITH);

        assertThat(Arrays.stream(BuildingType.values()).filter(BuildingType::isDefensive).toList())
                .as("defences other than the Monolith cost gold")
                .filteredOn(type -> type != BuildingType.MONOLITH)
                .allMatch(type -> type.upgradeResource() == ResourceType.GOLD);
    }

    @Test
    @DisplayName("The merged defences are available.")
    void mergedDefencesExist() {
        assertThat(List.of(BuildingType.RICOCHET_CANNON,
                        BuildingType.MULTI_ARCHER_TOWER,
                        BuildingType.MULTI_GEAR_TOWER))
                .allMatch(BuildingType::isDefensive);
    }

    @ParameterizedTest
    @EnumSource(value = BuildingType.class,
            names = {"GOLD_MINE", "ELIXIR_COLLECTOR", "DARK_ELIXIR_DRILL",
                    "GOLD_STORAGE", "ELIXIR_STORAGE", "DARK_ELIXIR_STORAGE"})
    @DisplayName("A resource building is neither a defence nor a camp.")
    void resourceBuildingsAreTheirOwnFamily(BuildingType type) {
        assertThat(type.isResourceBuilding()).isTrue();
        assertThat(type.isDefensive()).isFalse();
        assertThat(type.storesTroops()).isFalse();
    }

    @Test
    @DisplayName("Only the military camp holds troops, and its capacity follows the level.")
    void onlyTheCampStoresTroops() {
        assertThat(Arrays.stream(BuildingType.values()).filter(BuildingType::storesTroops).toList())
                .containsExactly(BuildingType.MILITARY_CAMP);

        assertThat(BuildingType.MILITARY_CAMP.housingCapacityAt(2))
                .isGreaterThan(BuildingType.MILITARY_CAMP.housingCapacityAt(1));

        assertThat(BuildingType.CANNON.housingCapacityAt(1)).isZero();
    }

    @Test
    @DisplayName("Out of bounds levels are rejected.")
    void outOfBoundsLevelsAreRejected() {
        assertThatThrownBy(() -> BuildingType.CANNON.statsAt(0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> BuildingType.CANNON.statsAt(BuildingType.CANNON.maxLevel() + 1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> BuildingType.CANNON.upgradeCostFrom(BuildingType.CANNON.maxLevel()))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("A building type is parsed from its code, whatever the case.")
    void parsesItsOwnCode(BuildingType type) {
        assertThat(BuildingType.from(type.name())).isEqualTo(type);
        assertThat(BuildingType.from(type.name().toLowerCase())).isEqualTo(type);
    }

    @Test
    @DisplayName("An unknown code is rejected with the list of valid ones, not a class name.")
    void unknownCodesListTheValidValues() {
        assertThatThrownBy(() -> BuildingType.from("DEATH_STAR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DEATH_STAR")
                .hasMessageContaining("CANNON")
                .hasMessageNotContaining("com.rolandhuon");
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("A building shoots at something exactly when it is a defence.")
    void targetScopeAndFirepowerAgree(BuildingType type) {
        assertThat(type.targetScope() != TargetScope.NONE)
                .as("%s: defensive=%s, targets=%s", type.label(), type.isDefensive(), type.targetScope())
                .isEqualTo(type.isDefensive());
    }

    @Test
    @DisplayName("The air defence only hits air, the cannon only hits ground, the archer tower hits both.")
    void eachDefenceHitsWhatItIsMeantTo() {
        assertThat(BuildingType.AIR_DEFENSE.canTarget(Movement.AIR)).isTrue();
        assertThat(BuildingType.AIR_DEFENSE.canTarget(Movement.GROUND)).isFalse();

        assertThat(BuildingType.CANNON.canTarget(Movement.GROUND)).isTrue();
        assertThat(BuildingType.CANNON.canTarget(Movement.AIR)).isFalse();

        assertThat(BuildingType.ARCHER_TOWER.canTarget(Movement.GROUND)).isTrue();
        assertThat(BuildingType.ARCHER_TOWER.canTarget(Movement.AIR)).isTrue();
    }

    @Test
    @DisplayName("A building that is not a defence never hits anything.")
    void aNonDefenceHitsNothing() {
        for (Movement movement : Movement.values()) {
            assertThat(BuildingType.GOLD_MINE.canTarget(movement)).isFalse();
            assertThat(BuildingType.LABORATORY.canTarget(movement)).isFalse();
        }
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("Every building costs something to put up.")
    void everyBuildingHasABuildCost(BuildingType type) {
        assertThat(type.buildCost())
                .as("%s build cost", type.label())
                .isPositive();
    }

    @Test
    @DisplayName("The build cost is the level-one entry, not the first upgrade.")
    void buildCostComesFromTheFirstLevel() {
        assertThat(BuildingType.CANNON.buildCost()).isEqualTo(BuildingType.CANNON.statsAt(1).upgradeCost());
        assertThat(BuildingType.CANNON.buildCost()).isNotEqualTo(BuildingType.CANNON.upgradeCostFrom(1));
    }

    @Test
    @DisplayName("One producer per currency, each in its own currency.")
    void oneProducerPerCurrency() {
        assertThat(Arrays.stream(BuildingType.values()).filter(BuildingType::produces).toList())
                .containsExactlyInAnyOrder(BuildingType.GOLD_MINE,
                        BuildingType.ELIXIR_COLLECTOR,
                        BuildingType.DARK_ELIXIR_DRILL);

        assertThat(BuildingType.GOLD_MINE.producedResource()).isEqualTo(ResourceType.GOLD);
        assertThat(BuildingType.ELIXIR_COLLECTOR.producedResource()).isEqualTo(ResourceType.ELIXIR);
        assertThat(BuildingType.DARK_ELIXIR_DRILL.producedResource()).isEqualTo(ResourceType.DARK_ELIXIR);

        assertThat(Arrays.stream(ResourceType.values()).toList())
                .as("every currency has exactly one producer")
                .allMatch(resource -> Arrays.stream(BuildingType.values())
                        .filter(BuildingType::produces)
                        .filter(type -> type.producedResource() == resource)
                        .count() == 1);

        assertThat(BuildingType.GOLD_MINE.upgradeResource())
                .as("a gold mine is paid for in elixir but produces gold")
                .isNotEqualTo(BuildingType.GOLD_MINE.producedResource());
    }

    @Test
    @DisplayName("Every currency has a storage of its own, and it actually holds something.")
    void everyCurrencyHasAStorage() {
        assertThat(Arrays.stream(BuildingType.values()).filter(BuildingType::stores).toList())
                .containsExactlyInAnyOrder(BuildingType.GOLD_STORAGE,
                        BuildingType.ELIXIR_STORAGE,
                        BuildingType.DARK_ELIXIR_STORAGE);

        assertThat(BuildingType.GOLD_STORAGE.storedResource()).isEqualTo(ResourceType.GOLD);
        assertThat(BuildingType.ELIXIR_STORAGE.storedResource()).isEqualTo(ResourceType.ELIXIR);
        assertThat(BuildingType.DARK_ELIXIR_STORAGE.storedResource()).isEqualTo(ResourceType.DARK_ELIXIR);

        for (BuildingType type : List.of(BuildingType.GOLD_STORAGE,
                BuildingType.ELIXIR_STORAGE, BuildingType.DARK_ELIXIR_STORAGE)) {

            assertThat(type.isResourceBuilding()).isTrue();
            assertThat(type.produces()).isFalse();
            assertThat(type.isDefensive()).isFalse();

            for (int level = 2; level <= type.maxLevel(); level++) {
                assertThat(type.storageCapacityAt(level))
                        .as("%s level %d capacity", type.label(), level)
                        .isGreaterThan(type.storageCapacityAt(level - 1));
            }
        }
    }

    @Test
    @DisplayName("The splash defences carry a splash profile, the single-target ones do not.")
    void splashDefencesDeclareIt() {
        for (BuildingType type : List.of(BuildingType.MORTAR, BuildingType.WIZARD_TOWER,
                BuildingType.MULTI_GEAR_TOWER, BuildingType.RICOCHET_CANNON)) {
            assertThat(type.attackProfile().splashTargets())
                    .as("%s should catch bystanders", type.label())
                    .isPositive();
        }

        for (BuildingType type : List.of(BuildingType.CANNON, BuildingType.ARCHER_TOWER,
                BuildingType.AIR_DEFENSE, BuildingType.MONOLITH)) {
            assertThat(type.attackProfile().splashTargets())
                    .as("%s hits one target", type.label())
                    .isZero();
        }

        assertThat(BuildingType.LABORATORY.attackProfile().splashTargets())
                .as("a building that does not shoot never splashes")
                .isZero();
    }

    @ParameterizedTest
    @EnumSource(value = BuildingType.class, names = {"GOLD_MINE", "ELIXIR_COLLECTOR", "DARK_ELIXIR_DRILL"})
    @DisplayName("A producer gets faster with every level, and holds six hours of it.")
    void productionGrowsWithLevel(BuildingType type) {
        for (int level = 1; level <= type.maxLevel(); level++) {
            assertThat(type.productionPerHourAt(level)).isPositive();
            assertThat(type.mineCapacityAt(level))
                    .isEqualTo(type.productionPerHourAt(level) * Production.STORAGE_HOURS);

            if (level > 1) {
                assertThat(type.productionPerHourAt(level))
                        .isGreaterThan(type.productionPerHourAt(level - 1));
            }
        }
    }

    @Test
    @DisplayName("A building that does not produce says so with a zero rate.")
    void nonProducersHaveNoRate() {
        assertThat(BuildingType.CANNON.produces()).isFalse();
        assertThat(BuildingType.CANNON.productionPerHourAt(1)).isZero();
        assertThat(BuildingType.CANNON.mineCapacityAt(1)).isZero();
        assertThat(BuildingType.CANNON.storageCapacityAt(1)).isZero();
        assertThat(BuildingType.CANNON.producedResource()).isNull();
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("Putting up a building never costs more than improving one.")
    void buildingIsCheaperThanUpgrading(BuildingType type) {
        assertThat(type.buildCost())
                .as("%s: build %d, first upgrade %d", type.label(), type.buildCost(), type.upgradeCostFrom(1))
                .isLessThanOrEqualTo(type.upgradeCostFrom(1));
    }

    @ParameterizedTest
    @EnumSource(BuildingType.class)
    @DisplayName("A building put up above level one is charged the whole ladder.")
    void costUpToSumsTheLadder(BuildingType type) {
        assertThat(type.costUpTo(1)).isEqualTo(type.buildCost());

        for (int level = 2; level <= type.maxLevel(); level++) {
            assertThat(type.costUpTo(level))
                    .as("%s up to level %d", type.label(), level)
                    .isEqualTo(type.costUpTo(level - 1) + type.upgradeCostFrom(level - 1))
                    .isGreaterThan(type.costUpTo(level - 1));
        }
    }

    @Test
    @DisplayName("A level nobody can reach cannot be paid for either.")
    void costUpToRejectsImpossibleLevels() {
        assertThatThrownBy(() -> BuildingType.CANNON.costUpTo(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> BuildingType.CANNON.costUpTo(BuildingType.CANNON.maxLevel() + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
