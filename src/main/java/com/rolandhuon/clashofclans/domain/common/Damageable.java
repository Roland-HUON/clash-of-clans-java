package com.rolandhuon.clashofclans.domain.common;

public interface Damageable extends Named {
    int getHp();
    void takeDamage(int amount);
    boolean isAlive();
}
