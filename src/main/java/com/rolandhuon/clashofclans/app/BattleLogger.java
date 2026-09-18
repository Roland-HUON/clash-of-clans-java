package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.battle.UnitDiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BattleLogger {

    private static final Logger log = LoggerFactory.getLogger(BattleLogger.class);

    @EventListener
    public void onUnitDied(UnitDiedEvent event){
        log.debug("{} is dead.", event.dead().getName());
    }
}
