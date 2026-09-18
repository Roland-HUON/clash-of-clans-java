package com.rolandhuon.clashofclans.domain.common;

public interface Attacker {
    int getDps();
    void attack(Damageable target);

    default boolean canTarget(Damageable target) {
        return true;
    }

    default AttackProfile attackProfile() {
        return AttackProfile.single(0);
    }
}
