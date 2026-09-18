package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.troop.StandardTroop;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.troop.TroopType;
import com.rolandhuon.clashofclans.domain.village.Village;
import com.rolandhuon.clashofclans.service.BattleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("demo")
@Component
public class GameCliRunner implements CommandLineRunner {

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

        System.out.println("=== RAID ===");
        System.out.println("Army             : " + army1.size() + " troops");
        System.out.println("Village          : " + village2.aliveTargets().size() + " buildings");
        System.out.println();

        BattleResult result = battleService.fight(army1, village2);

        System.out.println();
        System.out.println("=== RESULT ===");
        System.out.println("Destruction      : " + result.destructionPercentage() + "%");
        System.out.println("Stars            : " + result.stars());
        System.out.println("Turns            : " + result.turns());
        System.out.println("Surviving troops : " + result.survivingTroops() + " / " + army1.size());
    }
}
