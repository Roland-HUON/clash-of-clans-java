package com.rolandhuon.clashofclans.domain.battle;

public record TrophyExchange(int attackerDelta, int defenderDelta) {

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
