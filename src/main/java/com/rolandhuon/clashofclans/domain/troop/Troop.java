package com.rolandhuon.clashofclans.domain.troop;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Healable;
import com.rolandhuon.clashofclans.domain.common.Mobile;

public interface Troop extends Attacker, Mobile, Healable {
    void upgrade();
    void onDeath();
    int getHousingSpace();
    TargetingMode targetingMode();
    TroopRole role();
}
