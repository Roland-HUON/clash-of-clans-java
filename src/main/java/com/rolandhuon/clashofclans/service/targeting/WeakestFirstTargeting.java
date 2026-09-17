package com.rolandhuon.clashofclans.service.targeting;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.WEAKEST_FIRST;

@Component
public class WeakestFirstTargeting implements TargetingStrategy {
    @Override
    public Optional<Damageable> chooseTarget(List<Damageable> targets){
        return targets.stream().filter(Damageable::isAlive).min(Comparator.comparingInt((Damageable::getHp)));
    }

    @Override
    public TargetingMode mode() {
        return WEAKEST_FIRST;
    }
}
