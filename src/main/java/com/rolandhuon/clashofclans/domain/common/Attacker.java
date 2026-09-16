package com.rolandhuon.clashofclans.domain.common;

public interface Attacker {
    int getDps();
    void attack(Damageable target);
}
