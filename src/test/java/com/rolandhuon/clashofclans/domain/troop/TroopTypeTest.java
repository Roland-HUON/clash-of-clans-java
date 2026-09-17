package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TroopTypeTest {

    @ParameterizedTest
    @EnumSource(TroopType.class)
    @DisplayName("Hit points and damage never decrease when the level goes up.")
    void statsNeverDecreaseWithLevel(TroopType type) {
        for (int level = 2; level <= type.maxLevel(); level++) {
            TroopStats previous = type.statsAt(level - 1);
            TroopStats current = type.statsAt(level);

            assertThat(current.hp())
                    .as("%s level %d: hit points", type.label(), level)
                    .isGreaterThanOrEqualTo(previous.hp());

            assertThat(current.dps())
                    .as("%s level %d: damage", type.label(), level)
                    .isGreaterThanOrEqualTo(previous.dps());
        }
    }

    @ParameterizedTest
    @EnumSource(TroopType.class)
    @DisplayName("Every troop type exposes a usable attack profile.")
    void everyTypeExposesAnAttackProfile(TroopType type) {
        AttackProfile profile = type.attackProfile();

        assertThat(profile)
                .as("%s has no attack profile", type.label())
                .isNotNull();

        assertThat(profile.range())
                .as("%s: range", type.label())
                .isPositive();
    }

    @Test
    @DisplayName("statsAt rejects levels outside the table bounds.")
    void statsAtRejectsOutOfBoundsLevels() {
        assertThatThrownBy(() -> TroopType.BARBARIAN.statsAt(0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> TroopType.BARBARIAN.statsAt(TroopType.BARBARIAN.maxLevel() + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
