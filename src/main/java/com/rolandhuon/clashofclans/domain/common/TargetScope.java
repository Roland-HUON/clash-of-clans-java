package com.rolandhuon.clashofclans.domain.common;

public enum TargetScope {

    NONE,
    GROUND_ONLY,
    AIR_ONLY,
    ALL;

    public boolean covers(Movement movement) {
        return switch (this) {
            case NONE -> false;
            case GROUND_ONLY -> movement == Movement.GROUND;
            case AIR_ONLY -> movement == Movement.AIR;
            case ALL -> true;
        };
    }
}
