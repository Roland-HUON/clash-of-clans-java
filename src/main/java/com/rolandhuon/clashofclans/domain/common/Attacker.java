package com.rolandhuon.clashofclans.domain.common;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;

public interface Attacker {

    default TargetingMode targetingMode() {
        return TargetingMode.FIRST_ALIVE;
    }
    int getDps();
    void attack(Damageable target);

    default boolean canTarget(Damageable target) {
        return true;
    }

    default AttackProfile attackProfile() {
        return AttackProfile.single(0);
    }
}
