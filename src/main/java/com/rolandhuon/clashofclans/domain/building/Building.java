package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.Damageable;

public interface Building extends Damageable {
    void upgrade();
    void onDeath();
}
