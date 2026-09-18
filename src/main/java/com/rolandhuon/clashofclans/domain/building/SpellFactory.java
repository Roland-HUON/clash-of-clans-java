package com.rolandhuon.clashofclans.domain.building;

public class SpellFactory extends AbstractBuilding {

    public SpellFactory(){
        this(1);
    }

    public SpellFactory(int level) {
        super(BuildingType.SPELL_FACTORY, level);
    }
}
