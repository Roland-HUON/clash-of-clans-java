package com.rolandhuon.clashofclans.domain.troop;

public record TroopStats(int hp, int dps, int elixirCost) {

    public TroopStats{
        if(hp <= 0 || dps < 0) throw new IllegalArgumentException("No valid stats !");
        if(elixirCost < 0) throw new IllegalArgumentException("Upgrade cost must be >= 0");
    }
}
