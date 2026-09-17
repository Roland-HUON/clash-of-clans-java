package com.rolandhuon.clashofclans.service.targeting;

import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FirstAliveTargeting implements TargetingStrategy {
    @Override
    public Optional<Damageable> chooseTarget(List<Damageable> targets){
        return targets.stream().filter(Damageable::isAlive).findFirst();
    }
}
