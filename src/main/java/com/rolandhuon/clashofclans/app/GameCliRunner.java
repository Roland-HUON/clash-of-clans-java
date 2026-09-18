package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import com.rolandhuon.clashofclans.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("demo")
@Component
public class GameCliRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(GameCliRunner.class);

    private final BattleService battleService;

    public GameCliRunner(BattleService battleService){
        this.battleService = battleService;
    }

    @Override
    public void run(String... args) throws Exception {

        final List<Troop> army1 = new ArrayList<>();
        final Village village2 = new Village();

        for(int i = 0; i<13; i++){
            var barbarian = new StandardTroop(TroopType.BARBARIAN, 13);
            army1.add(barbarian);
        }
        for(int i = 0; i<13; i++){
            var archer = new StandardTroop(TroopType.ARCHER, 14);
            army1.add(archer);
        }


        var laboratory2 = new Laboratory(15);
        var herohall2 = new HeroHall(12);
        village2.addBuilding(laboratory2);
        village2.addBuilding(herohall2);

        log.info("{}", "=== RAID ===");
        log.info("{}", "Army             : " + army1.size() + " troops");
        log.info("{}", "Village          : " + village2.aliveTargets().size() + " buildings");
        log.info("");

        BattleResult result = battleService.fight(army1, village2);

        log.info("");
        log.info("{}", "=== RESULT ===");
        log.info("{}", "Destruction      : " + result.destructionPercentage() + "%");
        log.info("{}", "Stars            : " + result.stars());
        log.info("{}", "Turns            : " + result.turns());
        log.info("{}", "Surviving troops : " + result.survivingTroops() + " / " + army1.size());
    }
}
