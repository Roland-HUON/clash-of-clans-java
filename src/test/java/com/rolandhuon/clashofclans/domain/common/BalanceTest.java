package com.rolandhuon.clashofclans.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceTest {

    @Test
    @DisplayName("Adding to a balance works like addition.")
    void itAdds() {
        assertThat(Balance.plus(100, 50)).isEqualTo(150);
        assertThat(Balance.plus(0, 0)).isZero();
        assertThat(Balance.plus(999_999_999_999L, 1)).isEqualTo(1_000_000_000_000L);
    }

    @Test
    @DisplayName("A balance saturates instead of wrapping around into a negative.")
    void itSaturatesInsteadOfWrapping() {
        assertThat(Balance.plus(Long.MAX_VALUE, 1)).isEqualTo(Long.MAX_VALUE);
        assertThat(Balance.plus(Long.MAX_VALUE, Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
        assertThat(Balance.plus(Long.MAX_VALUE - 5, 100))
                .as("a saturated balance is never negative")
                .isPositive();
    }

    @Test
    @DisplayName("A negative amount is rejected.")
    void negativeAmountsAreRejected() {
        assertThatThrownBy(() -> Balance.plus(10, -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
