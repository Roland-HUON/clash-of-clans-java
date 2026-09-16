package com.rolandhuon.clashofclans.domain.troop;

public class Archer extends AbstractTroop{
    public Archer(){
        this(1);
    }

    public Archer(int level) {
        super(TroopType.ARCHER, level);
    }
}
