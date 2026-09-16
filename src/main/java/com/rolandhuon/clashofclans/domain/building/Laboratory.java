package com.rolandhuon.clashofclans.domain.building;

public class Laboratory extends AbstractBuilding {
    public Laboratory(){
        this(1);
    }
    public Laboratory(int level) {
        super(BuildingType.LABORATORY, level);
    }
}
