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
        VillageBuilding building = new VillageBuilding(BuildingType.GOLD_MINE, level);
        building.collect(BUILT_AT);
        return building;
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
            int capacity = BuildingType.GOLD_MINE.storageCapacityAt(1);

            assertThat(mine.pendingProduction(after(Duration.ofHours(Production.STORAGE_HOURS))))
                    .isEqualTo(capacity);
            assertThat(mine.pendingProduction(after(Duration.ofDays(30))))
                    .as("a month of neglect is worth no more than a full mine")
                    .isEqualTo(capacity);
        }

        @Test
        @DisplayName("A building that produces nothing never accrues anything.")
        void nonProducersNeverAccrue() {
            VillageBuilding cannon = new VillageBuilding(BuildingType.CANNON, 1);

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
        @DisplayName("Collecting an empty mine pays nothing and loses nothing.")
        void collectingTooEarlyPaysNothing() {
            VillageBuilding mine = mine(1);

            assertThat(mine.collect(BUILT_AT)).isZero();
            assertThat(mine.pendingProduction(after(Duration.ofHours(1))))
                    .isEqualTo(BuildingType.GOLD_MINE.productionPerHourAt(1));
        }
    }
}
