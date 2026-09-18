package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.config.BattleProperties;
import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.battle.TargetingMode;
import com.rolandhuon.clashofclans.domain.battle.TargetingStrategy;
import com.rolandhuon.clashofclans.domain.common.Attacker;
import com.rolandhuon.clashofclans.domain.common.Damageable;
import com.rolandhuon.clashofclans.domain.common.Healable;
import com.rolandhuon.clashofclans.domain.troop.TroopRole;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BattleService {

    private final CombatService combatService;
    private final Map<TargetingMode, TargetingStrategy> strategies;
    private final BattleProperties battleProperties;
    public BattleService(CombatService combatService, List<TargetingStrategy>  strategies, BattleProperties battleProperties) {
        this.combatService = combatService;
        this.strategies = strategies.stream().collect(Collectors.toMap(TargetingStrategy::mode, s -> s));
        List<TargetingMode> missing = Stream.concat(
                        Arrays.stream(TroopType.values()).map(TroopType::targetingMode),
                        Arrays.stream(BuildingType.values())
                                .filter(BuildingType::isDefensive)
                                .map(BuildingType::targetingMode))
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
                if(!troop.isAlive()) continue;

                if(troop.role() == TroopRole.SUPPORT){
                    healWeakestAlly(troop, army);
                    continue;
                }

                List<Damageable> reachable = village.aliveTargets();
                Optional<Damageable> enemy = strategies.get(troop.targetingMode()).chooseTarget(reachable);
                if(enemy.isEmpty()) continue;
                combatService.resolveAttack(troop, enemy.get(), reachable);
            }

            returnFire(village, army);
            turns++;
        }
        int remainingTroops = (int) army.stream()
                .filter(t -> t.isAlive())
                .count();
        return new BattleResult(village.destructionPercentage(), turns, remainingTroops);
    }

    private void returnFire(Village village, List<Troop> army) {
        for (Attacker defender : village.aliveDefenders()) {
            TargetingStrategy defenderStrategy = strategies.get(defender.targetingMode());

            List<Damageable> reachableTroops = army.stream()
                    .filter(Troop::isAlive)
                    .map(Damageable.class::cast)
                    .filter(defender::canTarget)
                    .toList();

            if (reachableTroops.isEmpty()) continue;

            defenderStrategy.chooseTarget(reachableTroops)
                    .ifPresent(target -> combatService.resolveAttack(defender, target, reachableTroops));
        }
    }

    private void healWeakestAlly(Troop healer, List<Troop> army) {
        List<Damageable> wounded = army.stream()
                .filter(Troop::isAlive)
                .filter(ally -> ally != healer)
                .filter(ally -> ally.getHp() < ally.getMaxHp())
                .map(Damageable.class::cast)
                .toList();

        if (wounded.isEmpty()) return;

        strategies.get(healer.targetingMode())
                .chooseTarget(wounded)
                .ifPresent(target -> ((Healable) target).heal(healer.getDps()));
    }
}
