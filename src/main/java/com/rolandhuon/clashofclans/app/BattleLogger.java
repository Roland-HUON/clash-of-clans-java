package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.battle.UnitDiedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BattleLogger {
    @EventListener
    public void onUnitDied(UnitDiedEvent event){
        System.out.println(event.dead().getName() + " is dead.");
    }
}
