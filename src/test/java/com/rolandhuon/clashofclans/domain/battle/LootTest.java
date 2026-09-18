package com.rolandhuon.clashofclans.domain.battle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LootTest {

    @ParameterizedTest
    @CsvSource({"0, 1000, 0", "50, 1000, 500", "100, 1000, 1000", "37, 1000, 370"})
    @DisplayName("The loot is the destruction share of the stock.")
    void lootIsProportionalToDestruction(int percentage, int stock, int expected) {
        assertThat(Loot.proportionalTo(percentage, stock, stock, stock).gold()).isEqualTo(expected);
    }

    @Test
    @DisplayName("A huge stock does not overflow into a wrong or negative loot.")
    void aHugeStockDoesNotOverflow() {
        long stock = 999_999_999_999L;

        assertThat(Loot.proportionalTo(100, stock, stock, stock).gold()).isEqualTo(stock);
        assertThat(Loot.proportionalTo(50, stock, 0, 0).gold()).isEqualTo(499_999_999_999L);
        assertThat(Loot.proportionalTo(37, 100_000_000L, 0, 0).gold()).isEqualTo(37_000_000L);

        assertThat(Loot.proportionalTo(13, Long.MAX_VALUE, 0, 0).gold())
                .as("even the largest possible stock stays positive")
                .isPositive();
    }

    @Test
    @DisplayName("An impossible percentage is rejected.")
    void impossiblePercentagesAreRejected() {
        assertThatThrownBy(() -> Loot.proportionalTo(101, 10, 10, 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Loot.proportionalTo(-1, 10, 10, 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Loot(-1, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("An empty loot knows it is empty.")
    void anEmptyLootKnowsIt() {
        assertThat(Loot.none().isEmpty()).isTrue();
        assertThat(Loot.proportionalTo(0, 1000, 1000, 1000).isEmpty()).isTrue();
        assertThat(Loot.proportionalTo(100, 1, 0, 0).isEmpty()).isFalse();
    }
}
