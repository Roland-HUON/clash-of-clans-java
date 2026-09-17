package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.building.HeroHall;
import com.rolandhuon.clashofclans.domain.building.Laboratory;
import com.rolandhuon.clashofclans.domain.troop.Archer;
import com.rolandhuon.clashofclans.domain.troop.Barbarian;
import com.rolandhuon.clashofclans.domain.troop.Troop;
import com.rolandhuon.clashofclans.domain.village.Village;
import com.rolandhuon.clashofclans.service.BattleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameCliRunner implements CommandLineRunner {

    private final BattleService battleService;

    public GameCliRunner(BattleService battleService){
        this.battleService = battleService;
    }

    @Override
    public void run(String... args) throws Exception {

        final List<Troop> army1 = new ArrayList<>();
        final List<Troop> army2 = new ArrayList<>();
        final Village village1 = new Village();
        final Village village2 = new Village();

        for(int i = 0; i<13; i++){
            var barbarian = new Barbarian(13);
            army1.add(barbarian);
        }
        for(int i = 0; i<10; i++){
            var archer = new Archer(14);
            army1.add(archer);
        }
        for(int i = 0; i<3; i++){
            var archer = new Archer(14);
            army1.add(archer);
        }

        /*for(int i = 0; i<17; i++){
            var barbarian = new Barbarian(13);
            army2.add(barbarian);
        }
        for(int i = 0; i<6; i++){
            var archer = new Archer(14);
            army2.add(archer);
        }
        var laboratory1 = new Laboratory(10);
        var herohall1 = new HeroHall(10);
        village1.addBuilding(laboratory1);
        village1.addBuilding(herohall1);*/

        var laboratory2 = new Laboratory(15);
        var herohall2 = new HeroHall(12);
        village2.addBuilding(laboratory2);
        village2.addBuilding(herohall2);

        System.out.println("=== RAID ===");
        System.out.println("Army   : " + army1.size() + " troops");
        System.out.println("Village : " + village2.aliveTargets().size() + " buildings");
        System.out.println();

        BattleResult result = battleService.fight(army1, village2);

        System.out.println();
        System.out.println("=== RESULT ===");
        System.out.println("Destruction  : " + result.destructionPercentage() + "%");
        System.out.println("Stars      : " + result.stars());
        System.out.println("Turns        : " + result.turns());
        System.out.println("Remainings troops   : " + result.survivingTroops() + " / " + army1.size());
    }
}
