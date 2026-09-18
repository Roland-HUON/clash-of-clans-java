package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.common.Mobile;

import java.util.Objects;

public class DefensiveBuilding extends AbstractBuilding implements Attacker {

    public DefensiveBuilding(BuildingType type, int level) {
        super(type, level);
        if (!type.isDefensive()) {
            throw new IllegalArgumentException(type.label() + " is not a defensive building");
        }
    }

    @Override
    public int getDps() {
        return getType().damageAt(getLevel());
    }

    @Override
    public boolean canTarget(Damageable target) {
        return !(target instanceof Mobile unit) || getType().canTarget(unit.movement());
    }

    @Override
    public void attack(Damageable target) {
        Objects.requireNonNull(target, "Need target");
        if (!isAlive()) throw new IllegalStateException("This building is destroyed");
        if (!target.isAlive()) return;
        if (!canTarget(target)) return;
        target.takeDamage(getDps());
    }
}
