package com.rolandhuon.clashofclans.domain.battle;

public record TrophyExchange(int attackerDelta, int defenderDelta) {

    public TrophyExchange cappedBy(int attackerTrophies, int defenderTrophies) {
        if (attackerTrophies < 0 || defenderTrophies < 0) {
            throw new IllegalArgumentException("A trophy count cannot be negative");
        }

        int delta = attackerDelta < 0
                ? -Math.min(-attackerDelta, attackerTrophies)
                : Math.min(attackerDelta, defenderTrophies);

        return new TrophyExchange(delta, -delta);
    }

    public static TrophyExchange forStars(int stars) {
        return switch (stars) {
            case 0 -> new TrophyExchange(-8, 8);
            case 1 -> new TrophyExchange(16, -16);
            case 2 -> new TrophyExchange(24, -24);
            case 3 -> new TrophyExchange(32, -32);
            default -> throw new IllegalArgumentException("Invalid star count: " + stars);
        };
    }
}
