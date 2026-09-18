package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.UnitDiedEvent;
import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CombatService {

    private final ApplicationEventPublisher events;

    public CombatService(ApplicationEventPublisher events){
        this.events = events;
    }

    public void resolveAttack(Attacker attacker, Damageable target, List<Damageable> battlefield){
        Objects.requireNonNull(target, "Need target");
        if(!target.isAlive()) return;

        strike(attacker, target);

        int splashing = attacker.attackProfile().splashTargets();
        if(splashing == 0) return;

        for(Damageable bystander : battlefield){
            if(splashing == 0) return;
            if(bystander == target || !bystander.isAlive()) continue;
            if(!attacker.canTarget(bystander)) continue;

            strike(attacker, bystander);
            splashing--;
        }
    }

    private void strike(Attacker attacker, Damageable target){
        attacker.attack(target);
        if(!target.isAlive()){
            events.publishEvent(new UnitDiedEvent(target));
        }
    }
}
