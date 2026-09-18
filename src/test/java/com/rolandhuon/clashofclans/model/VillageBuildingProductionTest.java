package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.Production;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VillageBuildingProductionTest {

    private static final Instant BUILT_AT = Instant.parse("2026-09-18T08:00:00Z");

    private VillageBuilding mine(int level) {
        return new VillageBuilding(BuildingType.GOLD_MINE, level, BUILT_AT);
    }

    private Instant after(Duration elapsed) {
        return BUILT_AT.plus(elapsed);
    }

    @Nested
    @DisplayName("accrual")
    class Accrual {

        @Test
        @DisplayName("A mine holds nothing the instant it is built.")
        void nothingAtTheStart() {
            assertThat(mine(1).pendingProduction(BUILT_AT)).isZero();
        }

        @Test
        @DisplayName("One hour of a level-one mine is one hour of its rate.")
        void oneHourIsOneRate() {
            assertThat(mine(1).pendingProduction(after(Duration.ofHours(1))))
                    .isEqualTo(BuildingType.GOLD_MINE.productionPerHourAt(1));
        }

        @Test
        @DisplayName("Half an hour is half the rate.")
        void partialHoursAreProRated() {
            assertThat(mine(1).pendingProduction(after(Duration.ofMinutes(30))))
                    .isEqualTo(BuildingType.GOLD_MINE.productionPerHourAt(1) / 2);
        }

        @Test
        @DisplayName("A higher level mine fills faster.")
        void higherLevelsFillFaster() {
            Duration anHour = Duration.ofHours(1);

            assertThat(mine(3).pendingProduction(after(anHour)))
                    .isGreaterThan(mine(1).pendingProduction(after(anHour)));
        }

        @Test
        @DisplayName("A mine stops filling once it holds its storage capacity.")
        void aFullMineStopsFilling() {
            VillageBuilding mine = mine(1);
            int capacity = BuildingType.GOLD_MINE.mineCapacityAt(1);

            assertThat(mine.pendingProduction(after(Duration.ofHours(Production.STORAGE_HOURS))))
                    .isEqualTo(capacity);
            assertThat(mine.pendingProduction(after(Duration.ofDays(30))))
                    .as("a month of neglect is worth no more than a full mine")
                    .isEqualTo(capacity);
        }

        @Test
        @DisplayName("A building that produces nothing never accrues anything.")
        void nonProducersNeverAccrue() {
            VillageBuilding cannon = new VillageBuilding(BuildingType.CANNON, 1, BUILT_AT);

            assertThat(cannon.pendingProduction(after(Duration.ofDays(1)))).isZero();
            assertThat(cannon.collect(after(Duration.ofDays(1)))).isZero();
        }
    }

    @Nested
    @DisplayName("collection")
    class Collection {

        @Test
        @DisplayName("Collecting empties the mine and starts the clock again.")
        void collectingEmptiesTheMine() {
            VillageBuilding mine = mine(2);
            Instant later = after(Duration.ofHours(2));
            int expected = BuildingType.GOLD_MINE.productionPerHourAt(2) * 2;

            assertThat(mine.collect(later)).isEqualTo(expected);
            assertThat(mine.pendingProduction(later)).isZero();
        }

        @Test
        @DisplayName("Two collections an hour apart each pay one hour.")
        void eachCollectionPaysItsOwnHour() {
            VillageBuilding mine = mine(1);
            int rate = BuildingType.GOLD_MINE.productionPerHourAt(1);

            assertThat(mine.collect(after(Duration.ofHours(1)))).isEqualTo(rate);
            assertThat(mine.collect(after(Duration.ofHours(2)))).isEqualTo(rate);
        }

        @Test
        @DisplayName("Collecting too often never costs you the minutes you had banked.")
        void collectingTooOftenLosesNothing() {
            VillageBuilding mine = mine(1);
            int rate = BuildingType.GOLD_MINE.productionPerHourAt(1);

            for (int i = 1; i <= 59; i++) {
                assertThat(mine.collect(after(Duration.ofSeconds(i))))
                        .as("nothing is due after %d seconds", i)
                        .isZero();
            }

            assertThat(mine.pendingProduction(after(Duration.ofHours(1))))
                    .as("an hour is still worth an hour, however often it was asked for")
                    .isEqualTo(rate);
        }

        @Test
        @DisplayName("Collecting twice in a row never pays twice.")
        void collectingTwiceNeverPaysTwice() {
            for (BuildingType type : new BuildingType[]{BuildingType.GOLD_MINE, BuildingType.DARK_ELIXIR_DRILL}) {
                for (int minutes : new int[]{1, 2, 7, 59, 61, 180}) {
                    VillageBuilding producer = new VillageBuilding(type, type.maxLevel(), BUILT_AT);
                    Instant when = after(Duration.ofMinutes(minutes));

                    int first = producer.collect(when);
                    int second = producer.collect(when);

                    assertThat(second)
                            .as("%s after %d min paid %d then %d", type.label(), minutes, first, second)
                            .isZero();
                    assertThat(producer.pendingProduction(when)).isZero();
                }
            }
        }

        @Test
        @DisplayName("A part-paid hour keeps its remainder for the next collection.")
        void theRemainderCarriesOver() {
            VillageBuilding mine = mine(1);
            int rate = BuildingType.GOLD_MINE.productionPerHourAt(1);

            assertThat(mine.collect(after(Duration.ofMinutes(90)))).isEqualTo(rate + rate / 2);
            assertThat(mine.pendingProduction(after(Duration.ofMinutes(150))))
                    .as("the hour that followed is due in full")
                    .isEqualTo(rate);
        }

        @Test
        @DisplayName("A mine left full past its cap forfeits the overflow.")
        void aFullMineForfeitsTheOverflow() {
            VillageBuilding mine = mine(1);
            int capacity = BuildingType.GOLD_MINE.mineCapacityAt(1);

            assertThat(mine.collect(after(Duration.ofDays(2)))).isEqualTo(capacity);
            assertThat(mine.pendingProduction(after(Duration.ofDays(2))))
                    .as("the clock restarts from the collection, not from two days ago")
                    .isZero();
        }

        @Test
        @DisplayName("Collecting an empty mine pays nothing and loses nothing.")
        void collectingTooEarlyPaysNothing() {
            VillageBuilding mine = mine(1);

            assertThat(mine.collect(BUILT_AT)).isZero();
            assertThat(mine.pendingProduction(after(Duration.ofHours(1))))
                    .isEqualTo(BuildingType.GOLD_MINE.productionPerHourAt(1));
        }
    }
}
