package com.rolandhuon.clashofclans.domain.troop;

public record TroopStats(int hp, int dps) {
    public TroopStats{
        if(hp <= 0 || dps < 0) throw new IllegalArgumentException("No valid stats !");
    }
}
