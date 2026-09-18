package com.rolandhuon.clashofclans.domain.building;


public class HeroHall extends AbstractBuilding {
    public HeroHall(){
        this(1);
    }
    public HeroHall(int level) {
        super(BuildingType.HEROHALL, level);
    }
}
