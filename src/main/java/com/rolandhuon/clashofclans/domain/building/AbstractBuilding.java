package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.entity.AbstractDamageableEntity;

public abstract class AbstractBuilding extends AbstractDamageableEntity<BuildingType> implements Building {
    protected AbstractBuilding(BuildingType type, int level){
        super(type, level);
    }
}
