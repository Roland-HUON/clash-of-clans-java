package com.rolandhuon.clashofclans.app;

import com.rolandhuon.clashofclans.domain.troop.Archer;
import com.rolandhuon.clashofclans.domain.troop.Barbarian;
import com.rolandhuon.clashofclans.service.CombatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameCliRunner implements CommandLineRunner {

    private final CombatService combatService;

    public GameCliRunner(CombatService combatService){
        this.combatService = combatService;
    }

    @Override
    public void run(String... args) throws Exception {
        var barbarian1 = new Barbarian(13);
        var archer1= new Archer(14);

        System.out.println(barbarian1.getName() + " vs " + archer1.getName());

        System.out.println("Barbarian stats : ");
        System.out.println("Unit : " +  barbarian1.getName());
        System.out.println("HP : " +  barbarian1.getHp());
        System.out.println("DPS : " +  barbarian1.getDps());
        System.out.println("Range : " +  barbarian1.getType().attackProfile().range());

        System.out.println("Archer stats : ");
        System.out.println("Unit : " +  archer1.getName());
        System.out.println("HP : " +  archer1.getHp());
        System.out.println("DPS : " +  archer1.getDps());
        System.out.println("Range : " +  archer1.getType().attackProfile().range());

        while(barbarian1.isAlive() && archer1.isAlive()){
            combatService.resolveAttack(barbarian1, archer1);
            System.out.println(barbarian1.getName() + " dealt " + barbarian1.getDps() + " to " + archer1.getName() + ". " + archer1.getHp() + "/" + archer1.getMaxHp());
            if(archer1.isAlive()) {
                combatService.resolveAttack(archer1, barbarian1);
                System.out.println(archer1.getName() + " dealt " + archer1.getDps() + " to " + barbarian1.getName() + ". " + barbarian1.getHp() + "/" + barbarian1.getMaxHp());
            }
        }
    }
}
