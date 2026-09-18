package com.rolandhuon.clashofclans.domain.building;

public class Monolith extends AbstractBuilding {

    public Monolith(){
        this(1);
    }

    public Monolith(int level) {
        super(BuildingType.MONOLITH, level);
    }
}
