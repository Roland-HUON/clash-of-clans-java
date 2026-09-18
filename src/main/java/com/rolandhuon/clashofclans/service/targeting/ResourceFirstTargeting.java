package com.rolandhuon.clashofclans.service.targeting;

import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.building.Building;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResourceFirstTargeting implements TargetingStrategy {

    @Override
    public Optional<Damageable> chooseTarget(List<Damageable> targets) {
        List<Damageable> alive = targets.stream().filter(Damageable::isAlive).toList();

        return alive.stream()
                .filter(t -> t instanceof Building building && building.getType().isResourceBuilding())
                .findFirst()
                .or(() -> alive.stream().findFirst());
    }

    @Override
    public TargetingMode mode() {
        return TargetingMode.RESOURCE_FIRST;
    }
}
