package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.spell.Spell;
import com.rolandhuon.clashofclans.domain.troop.Troop;

import java.util.List;

public class Laboratory {
    private static int maxLaboratoryCount = 1;

    int laboratoryCount;
    int pv;
    int level;
    List<Troop> troops;
    List<Spell> spells;
}
