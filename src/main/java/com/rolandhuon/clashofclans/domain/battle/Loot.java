package com.rolandhuon.clashofclans.domain.battle;

public record Loot(int gold, int elixir, int darkElixir) {

    public Loot {
        if (gold < 0 || elixir < 0 || darkElixir < 0) {
            throw new IllegalArgumentException("Loot must be >= 0");
        }
    }

    public static Loot proportionalTo(int destructionPercentage, int gold, int elixir, int darkElixir) {
        if (destructionPercentage < 0 || destructionPercentage > 100) {
            throw new IllegalArgumentException("Invalid percentage: " + destructionPercentage);
        }
        return new Loot(
                share(gold, destructionPercentage),
                share(elixir, destructionPercentage),
                share(darkElixir, destructionPercentage));
    }

    public static Loot none() {
        return new Loot(0, 0, 0);
    }

    public boolean isEmpty() {
        return gold == 0 && elixir == 0 && darkElixir == 0;
    }

    private static int share(int stock, int percentage) {
        return stock * percentage / 100;
    }
}
