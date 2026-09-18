package com.rolandhuon.clashofclans.domain.battle;

public record Loot(long gold, long elixir, long darkElixir) {

    public Loot {
        if (gold < 0 || elixir < 0 || darkElixir < 0) {
            throw new IllegalArgumentException("Loot must be >= 0");
        }
    }

    public static Loot proportionalTo(int destructionPercentage, long gold, long elixir, long darkElixir) {
        if (destructionPercentage < 0 || destructionPercentage > 100) {
            throw new IllegalArgumentException("Invalid percentage: " + destructionPercentage);
        }
        return new Loot(
                share(gold, destructionPercentage),
                share(elixir, destructionPercentage),
                share(darkElixir, destructionPercentage));
    }

    public boolean isEmpty() {
        return gold == 0 && elixir == 0 && darkElixir == 0;
    }

    private static long share(long stock, int percentage) {
        if (stock < 0) throw new IllegalArgumentException("Stock must be >= 0, was " + stock);
        return stock / 100 * percentage + stock % 100 * percentage / 100;
    }
}
