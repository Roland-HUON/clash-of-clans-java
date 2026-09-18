package com.rolandhuon.clashofclans.domain.village;

import com.rolandhuon.clashofclans.domain.common.ResourceType;

public record Harvest(long gold, long elixir, long darkElixir) {

    public Harvest {
        if (gold < 0 || elixir < 0 || darkElixir < 0) {
            throw new IllegalArgumentException("A harvest cannot be negative");
        }
    }

    public static Harvest nothing() {
        return new Harvest(0, 0, 0);
    }

    public Harvest plus(ResourceType resource, long amount) {
        return switch (resource) {
            case GOLD -> new Harvest(gold + amount, elixir, darkElixir);
            case ELIXIR -> new Harvest(gold, elixir + amount, darkElixir);
            case DARK_ELIXIR -> new Harvest(gold, elixir, darkElixir + amount);
        };
    }

    public boolean isEmpty() {
        return gold == 0 && elixir == 0 && darkElixir == 0;
    }
}
