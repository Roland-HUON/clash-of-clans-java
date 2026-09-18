package com.rolandhuon.clashofclans.domain.troop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractTroopTest {

    @Nested
    @DisplayName("takeDamage")
    class TakeDamage {

        @Test
        @DisplayName("Damage above hit points kills the troop without throwing.")
        void lethalDamageKillsWithoutThrowing() {
            StandardTroop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);
            int overkill = barbarian.getMaxHp() + 5;

            assertThatCode(() -> barbarian.takeDamage(overkill))
                    .doesNotThrowAnyException();

            assertThat(barbarian.getHp()).isZero();
            assertThat(barbarian.isAlive()).isFalse();
        }

        @Test
        @DisplayName("Negative damage is rejected and leaves the troop untouched.")
        void negativeDamageIsRejected() {
            StandardTroop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);

            assertThatThrownBy(() -> barbarian.takeDamage(-5))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(barbarian.getHp()).isEqualTo(barbarian.getMaxHp());
        }
    }

    @Nested
    @DisplayName("upgrade")
    class Upgrade {

        @Test
        @DisplayName("Upgrading applies the stats of the next level.")
        void upgradeAppliesNextLevelStats() {
            StandardTroop barbarian = new StandardTroop(TroopType.BARBARIAN, 1);
            TroopStats expected = TroopType.BARBARIAN.statsAt(2);

            barbarian.upgrade();

            assertThat(barbarian.getLevel()).isEqualTo(2);
            assertThat(barbarian.getMaxHp()).isEqualTo(expected.hp());
            assertThat(barbarian.getDps()).isEqualTo(expected.dps());
        }

        @Test
        @DisplayName("Upgrade stops at max level.")
        void upgradeStopsAtMaxLevel() {
            StandardTroop barbarian = new StandardTroop(TroopType.BARBARIAN, TroopType.BARBARIAN.maxLevel());

            assertThatThrownBy(barbarian::upgrade)
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
