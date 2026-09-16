package com.rolandhuon.clashofclans.domain.common;

public interface Damageable {
    int getHp();
    void takeDamage(int amount);
    boolean isAlive();
}
