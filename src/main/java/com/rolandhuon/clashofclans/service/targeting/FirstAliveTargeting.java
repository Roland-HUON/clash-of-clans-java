package com.rolandhuon.clashofclans.service.targeting;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.rolandhuon.clashofclans.domain.battle.TargetingMode.FIRST_ALIVE;

@Component
public class FirstAliveTargeting implements TargetingStrategy {
    @Override
    public Optional<Damageable> chooseTarget(List<Damageable> targets){
        return targets.stream().filter(Damageable::isAlive).findFirst();
    }

    @Override
    public TargetingMode mode() {
        return FIRST_ALIVE;
    }
}
