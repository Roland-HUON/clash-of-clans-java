package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.hero.Hero;

import java.util.List;

public class HeroHall extends AbstractBuilding {
    public HeroHall(){
        this(1);
    }
    public HeroHall(int level) {
        super(BuildingType.HEROHALL, level);
    }
}
