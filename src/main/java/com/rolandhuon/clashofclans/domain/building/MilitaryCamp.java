package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.troop.Troop;

import java.util.List;

public class MilitaryCamp {
    private static int maxMilitaryCampCount = 4; 

    int pv;
    int level;
    int housingSpaceAvailable;
    int militaryCampCount;
    List<Troop> troops;
}
