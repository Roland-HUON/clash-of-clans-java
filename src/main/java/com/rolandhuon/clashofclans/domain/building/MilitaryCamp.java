package com.rolandhuon.clashofclans.domain.building;

public class MilitaryCamp extends AbstractBuilding {

    public MilitaryCamp(){
        this(1);
    }

    public MilitaryCamp(int level) {
        super(BuildingType.MILITARY_CAMP, level);
    }
}
