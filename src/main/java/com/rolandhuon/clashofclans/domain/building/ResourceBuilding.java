package com.rolandhuon.clashofclans.domain.building;

public class ResourceBuilding extends AbstractBuilding {

    public ResourceBuilding(BuildingType type, int level) {
        super(type, level);
        if (!type.isResourceBuilding()) {
            throw new IllegalArgumentException(type.label() + " is not a resource building");
        }
    }
}
