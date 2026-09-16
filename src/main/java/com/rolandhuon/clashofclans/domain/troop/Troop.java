package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Damageable;

public interface Troop extends Attacker, Damageable {
    void upgrade();
    void onDeath();
    int getHousingSpace();
}
