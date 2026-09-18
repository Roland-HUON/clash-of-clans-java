package com.rolandhuon.clashofclans.domain.building;

public class PetHouse extends AbstractBuilding {

    public PetHouse(){
        this(1);
    }

    public PetHouse(int level) {
        super(BuildingType.PET_HOUSE, level);
    }
}
