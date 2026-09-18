package com.rolandhuon.clashofclans.domain.village;

import com.rolandhuon.clashofclans.domain.common.ResourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HarvestTest {

    @Test
    @DisplayName("Nothing harvested is empty.")
    void nothingIsEmpty() {
        assertThat(Harvest.nothing().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Each currency lands in its own pile.")
    void everyCurrencyHasItsOwnPile() {
        Harvest harvest = Harvest.nothing()
                .plus(ResourceType.GOLD, 100)
                .plus(ResourceType.ELIXIR, 50)
                .plus(ResourceType.DARK_ELIXIR, 5)
                .plus(ResourceType.GOLD, 20);

        assertThat(harvest.gold()).isEqualTo(120);
        assertThat(harvest.elixir()).isEqualTo(50);
        assertThat(harvest.darkElixir()).isEqualTo(5);
        assertThat(harvest.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("A negative harvest is rejected.")
    void negativeHarvestsAreRejected() {
        assertThatThrownBy(() -> new Harvest(-1, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
