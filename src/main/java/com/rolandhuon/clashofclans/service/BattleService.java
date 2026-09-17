package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.config.BattleProperties;
import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BattleService {

    private final CombatService combatService;
    private final Map<TargetingMode, TargetingStrategy> strategies;
    private final BattleProperties battleProperties;
    public BattleService(CombatService combatService, List<TargetingStrategy>  strategies, BattleProperties battleProperties) {
        this.combatService = combatService;
        this.strategies = strategies.stream().collect(Collectors.toMap(TargetingStrategy::mode, s -> s));
        List<TargetingMode> missing = Arrays.stream(TroopType.values())
                .map(TroopType::targetingMode)
                .distinct()
                .filter(mode -> !this.strategies.containsKey(mode))
                .toList();
        if(!missing.isEmpty()) throw new IllegalStateException("No TargetingStrategy for modes : " + missing.toString());
        this.battleProperties = battleProperties;
    }

    public BattleResult fight(List<Troop> army, Village village){
        int turns = 0;
        while(!village.isDestroyed() && turns < battleProperties.maxTurns()){
            List<Troop> aliveTroop = army.stream()
                    .filter(t -> t.isAlive())
                    .toList();
            if(aliveTroop.isEmpty()) break;
            for(Troop troop : aliveTroop){
                Optional<Damageable> enemy = strategies.get(troop.targetingMode()).chooseTarget(village.aliveTargets());
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
