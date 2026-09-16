package com.rolandhuon.clashofclans.domain.common;

public interface EntityType {
    String label();
    int maxLevel();
    int hpAt(int level);
}
