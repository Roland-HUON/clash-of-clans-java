package com.rolandhuon.clashofclans.domain.troop;

public class Barbarian extends AbstractTroop {
    public Barbarian(){
        this(1);
    }

    public Barbarian(int level) {
        super(TroopType.BARBARIAN, level);
    }
}
