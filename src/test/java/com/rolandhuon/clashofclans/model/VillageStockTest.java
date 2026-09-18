package com.rolandhuon.clashofclans.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VillageStockTest {

    @Test
    @DisplayName("A harvest raises the raidable stock.")
    void stockGrowsWithTheHarvest() {
        Village village = new Village("Test", 100, 50, 5);

        village.stock(900, 950, 95);

        assertThat(village.getGold()).isEqualTo(1000);
        assertThat(village.getElixir()).isEqualTo(1000);
        assertThat(village.getDarkElixir()).isEqualTo(100);
    }

    @Test
    @DisplayName("A stock at the ceiling saturates instead of turning negative.")
    void aFullStockNeverTurnsNegative() {
        Village village = new Village("Rich", Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);

        village.stock(1_000_000, 1_000_000, 1_000_000);

        assertThat(village.getGold()).isEqualTo(Long.MAX_VALUE);
        assertThat(village.getElixir()).isPositive();
        assertThat(village.getDarkElixir()).isPositive();
    }

    @Test
    @DisplayName("Looting never digs below zero.")
    void lootingStopsAtZero() {
        Village village = new Village("Poor", 10, 10, 10);

        village.loot(100, 100, 100);

        assertThat(village.getGold()).isZero();
        assertThat(village.getElixir()).isZero();
        assertThat(village.getDarkElixir()).isZero();
    }
}
