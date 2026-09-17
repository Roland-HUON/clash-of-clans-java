package com.rolandhuon.clashofclans.domain.battle;

import com.rolandhuon.clashofclans.domain.common.Damageable;

import java.util.List;
import java.util.Optional;

public interface TargetingStrategy{
    Optional<Damageable> chooseTarget(List<Damageable> targets);

    TargetingMode mode();
}
