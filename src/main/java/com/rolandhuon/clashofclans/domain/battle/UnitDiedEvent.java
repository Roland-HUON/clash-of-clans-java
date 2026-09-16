package com.rolandhuon.clashofclans.domain.battle;

import com.rolandhuon.clashofclans.domain.common.Damageable;

public record UnitDiedEvent(Damageable dead) {
}
