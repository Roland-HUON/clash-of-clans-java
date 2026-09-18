package com.rolandhuon.clashofclans.service.targeting;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.DefensiveBuilding;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.building.ResourceBuilding;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TargetingStrategiesTest {

    private final Laboratory laboratory = new Laboratory(1);
    private final DefensiveBuilding cannon = new DefensiveBuilding(BuildingType.CANNON, 1);
    private final ResourceBuilding goldMine = new ResourceBuilding(BuildingType.GOLD_MINE, 1);

    private List<Damageable> village() {
        return List.of(laboratory, cannon, goldMine);
    }

    @Test
    @DisplayName("Each strategy declares the mode it answers for.")
    void eachStrategyDeclaresItsMode() {
        assertThat(new FirstAliveTargeting().mode()).isEqualTo(TargetingMode.FIRST_ALIVE);
        assertThat(new WeakestFirstTargeting().mode()).isEqualTo(TargetingMode.WEAKEST_FIRST);
        assertThat(new DefenseFirstTargeting().mode()).isEqualTo(TargetingMode.DEFENSE_FIRST);
        assertThat(new ResourceFirstTargeting().mode()).isEqualTo(TargetingMode.RESOURCE_FIRST);
    }

    @Nested
    @DisplayName("priority")
    class Priority {

        @Test
        @DisplayName("DEFENSE_FIRST walks past the other buildings to reach a defence.")
        void defenseFirstPicksTheDefence() {
            assertThat(new DefenseFirstTargeting().chooseTarget(village())).contains(cannon);
        }

        @Test
        @DisplayName("RESOURCE_FIRST walks past the other buildings to reach a collector.")
        void resourceFirstPicksTheCollector() {
            assertThat(new ResourceFirstTargeting().chooseTarget(village())).contains(goldMine);
        }

        @Test
        @DisplayName("FIRST_ALIVE takes whatever comes first.")
        void firstAliveTakesTheFirstOne() {
            assertThat(new FirstAliveTargeting().chooseTarget(village())).contains(laboratory);
        }

        @Test
        @DisplayName("WEAKEST_FIRST takes the lowest hit points.")
        void weakestFirstTakesTheLowestHitPoints() {
            Damageable expected = village().stream()
                    .min(Comparator.comparingInt(Damageable::getHp))
                    .orElseThrow();

            assertThat(new WeakestFirstTargeting().chooseTarget(village())).contains(expected);
        }
    }

    @Nested
    @DisplayName("fallback")
    class Fallback {

        @Test
        @DisplayName("Without its favourite target, a strategy falls back on what is left.")
        void fallsBackWhenTheFavouriteTargetIsMissing() {
            List<Damageable> noDefenceNoResource = List.of(laboratory);

            assertThat(new DefenseFirstTargeting().chooseTarget(noDefenceNoResource)).contains(laboratory);
            assertThat(new ResourceFirstTargeting().chooseTarget(noDefenceNoResource)).contains(laboratory);
        }

        @Test
        @DisplayName("A destroyed building is never targeted.")
        void destroyedBuildingsAreSkipped() {
            cannon.takeDamage(cannon.getMaxHp());

            assertThat(new DefenseFirstTargeting().chooseTarget(village()))
                    .as("the destroyed cannon must be skipped")
                    .contains(laboratory);
        }

        @Test
        @DisplayName("An empty village gives no target at all.")
        void anEmptyVillageGivesNoTarget() {
            List<TargetingStrategy> all = List.of(new FirstAliveTargeting(),
                    new WeakestFirstTargeting(),
                    new DefenseFirstTargeting(),
                    new ResourceFirstTargeting());

            for (TargetingStrategy strategy : all) {
                assertThat(strategy.chooseTarget(List.of()))
                        .as("%s on an empty village", strategy.mode())
                        .isEmpty();
            }
        }
    }
}
