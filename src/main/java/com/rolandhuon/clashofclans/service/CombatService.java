package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.UnitDiedEvent;
import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class CombatService {

    private final ApplicationEventPublisher events;

    public CombatService(ApplicationEventPublisher events){
        this.events = events;
    }

    public void resolveAttack(Attacker attacker, Damageable target){
        Objects.requireNonNull(target, "Need target");
        if(!target.isAlive()) return;
        attacker.attack(target);
        if(!target.isAlive()){
            events.publishEvent(new UnitDiedEvent(target));
        }
    }
}
