package com.rolandhuon.clashofclans.domain.battle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrophyExchangeTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("Trophies are never created nor destroyed, whatever the outcome.")
    void everyExchangeIsZeroSum(int stars) {
        TrophyExchange exchange = TrophyExchange.forStars(stars);

        assertThat(exchange.attackerDelta() + exchange.defenderDelta())
                .as("%d star(s): %d / %d", stars, exchange.attackerDelta(), exchange.defenderDelta())
                .isZero();
    }

    @Test
    @DisplayName("The more stars, the more trophies the attacker takes.")
    void moreStarsMeanMoreTrophies() {
        assertThat(TrophyExchange.forStars(0).attackerDelta()).isNegative();

        for (int stars = 1; stars <= 3; stars++) {
            assertThat(TrophyExchange.forStars(stars).attackerDelta())
                    .isGreaterThan(TrophyExchange.forStars(stars - 1).attackerDelta());
        }
    }

    @Test
    @DisplayName("An impossible star count is rejected.")
    void impossibleStarCountsAreRejected() {
        assertThatThrownBy(() -> TrophyExchange.forStars(4)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TrophyExchange.forStars(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
