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

    @Test
    @DisplayName("A defender with nothing to lose costs the attacker nothing to take.")
    void anEmptyDefenderPaysNothing() {
        TrophyExchange threeStars = TrophyExchange.forStars(3).cappedBy(1500, 0);

        assertThat(threeStars.attackerDelta()).isZero();
        assertThat(threeStars.defenderDelta()).isZero();
    }

    @Test
    @DisplayName("An attacker with nothing to lose cannot go below zero either.")
    void anEmptyAttackerLosesNothing() {
        TrophyExchange defeat = TrophyExchange.forStars(0).cappedBy(0, 1500);

        assertThat(defeat.attackerDelta()).isZero();
        assertThat(defeat.defenderDelta()).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("Capping keeps the exchange zero-sum, whatever each side holds.")
    void cappingStaysZeroSum(int stars) {
        for (int attacker : new int[]{0, 3, 8, 20, 5000}) {
            for (int defender : new int[]{0, 3, 8, 20, 5000}) {
                TrophyExchange exchange = TrophyExchange.forStars(stars).cappedBy(attacker, defender);

                assertThat(exchange.attackerDelta() + exchange.defenderDelta())
                        .as("%d stars, attacker %d, defender %d", stars, attacker, defender)
                        .isZero();
                assertThat(attacker + exchange.attackerDelta()).isNotNegative();
                assertThat(defender + exchange.defenderDelta()).isNotNegative();
            }
        }
    }

    @Test
    @DisplayName("A partial loss takes only what the loser still has.")
    void aPartialLossTakesWhatIsLeft() {
        TrophyExchange exchange = TrophyExchange.forStars(3).cappedBy(1500, 10);

        assertThat(exchange.attackerDelta()).isEqualTo(10);
        assertThat(exchange.defenderDelta()).isEqualTo(-10);
    }

    @Test
    @DisplayName("A negative trophy count is rejected.")
    void negativeTrophyCountsAreRejected() {
        assertThatThrownBy(() -> TrophyExchange.forStars(3).cappedBy(-1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
