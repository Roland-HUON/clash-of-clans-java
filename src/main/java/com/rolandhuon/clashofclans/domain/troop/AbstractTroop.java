package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.common.Movement;
import com.rolandhuon.clashofclans.domain.entity.AbstractDamageableEntity;

import java.util.Objects;

public abstract class AbstractTroop extends AbstractDamageableEntity<TroopType> implements Troop {
    private int dps;

    public int getDps() {
        return dps;
    }

    protected AbstractTroop(TroopType type, int level){
        super(type, level);
    }

    @Override
    protected void applyLevel(int newLevel){
        super.applyLevel(newLevel);
        TroopStats stats = getType().statsAt(newLevel);
        this.dps = stats.dps();
    }

    public void attack(Damageable target){
        Objects.requireNonNull(target, "Need target");
        if(!isAlive()) throw new IllegalStateException("This troop is dead");
        if(!target.isAlive()) return;
        target.takeDamage(getDps());
    }

    public int getHousingSpace(){
        return getType().housingSpace();
    }

    public TargetingMode targetingMode(){ return getType().targetingMode(); }

    public TroopRole role(){ return getType().role(); }

    public Movement movement(){ return getType().movement(); }

    public AttackProfile attackProfile(){ return getType().attackProfile(); }
}
