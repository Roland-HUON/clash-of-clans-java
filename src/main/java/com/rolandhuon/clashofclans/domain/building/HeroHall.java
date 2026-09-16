package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.hero.Hero;

import java.util.List;

public class HeroHall {
    private static int maxHeroHallCount = 1;

    int heroHallCount;
    int pv;
    int level;
    List<Hero> heros;
}
