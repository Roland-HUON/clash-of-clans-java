package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.village.Village;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BattleService {

    private static final int MAX_TURNS = 180;

    private final CombatService combatService;
    private final TargetingStrategy targetingStrategy;

    public BattleService(CombatService combatService, TargetingStrategy targetingStrategy) {
        this.combatService = combatService;
        this.targetingStrategy = targetingStrategy;
    }

    public BattleResult fight(List<Troop> army, Village village){
        int turns = 0;
        while(!village.isDestroyed() && turns < MAX_TURNS){
            List<Troop> aliveTroop = army.stream()
                    .filter(t -> t.isAlive())
                    .toList();
            if(aliveTroop.isEmpty()) break;
            for(Troop troop : aliveTroop){
                Optional<Damageable> enemy = targetingStrategy.chooseTarget(village.aliveTargets());
                if(enemy.isEmpty()) break;
                combatService.resolveAttack(troop, enemy.get());
            }
            turns++;
        }
        int remainingTroops = (int) army.stream()
                .filter(t -> t.isAlive())
                .count();
        return new BattleResult(village.destructionPercentage(), turns, remainingTroops);
    }
}
