package com.rolandhuon.clashofclans.domain.player;

import com.rolandhuon.clashofclans.domain.hero.Hero;
import com.rolandhuon.clashofclans.domain.spell.Spell;
import com.rolandhuon.clashofclans.domain.troop.Troop;

import java.util.List;

public class Player {
    private static int ID_CPT = 1;

    int id;
    int level;
    int gold;
    int stealableGold;
    int elixir;
    int stealableElixir;
    int darkElixir;
    int stealableDarkElixir;
    List<Troop> troops;
    List<Spell> spells;
    List<Hero> heros;

    public Player(int level, int gold, int stealableGold, int elixir, int stealableElixir, int darkElixir, int stealableDarkElixir, List<Troop> troops, List<Spell> spells, List<Hero> heros) {
        this.id = ID_CPT++;
        this.level = level;
        this.gold = gold;
        this.stealableGold = stealableGold;
        this.elixir = elixir;
        this.stealableElixir = stealableElixir;
        this.darkElixir = darkElixir;
        this.stealableDarkElixir = stealableDarkElixir;
        this.troops = troops;
        this.spells = spells;
        this.heros = heros;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getStealableGold() {
        return stealableGold;
    }

    public void setStealableGold(int stealableGold) {
        this.stealableGold = stealableGold;
    }

    public int getElixir() {
        return elixir;
    }

    public void setElixir(int elixir) {
        this.elixir = elixir;
    }

    public int getStealableElixir() {
        return stealableElixir;
    }

    public void setStealableElixir(int stealableElixir) {
        this.stealableElixir = stealableElixir;
    }

    public int getDarkElixir() {
        return darkElixir;
    }

    public void setDarkElixir(int darkElixir) {
        this.darkElixir = darkElixir;
    }

    public int getStealableDarkElixir() {
        return stealableDarkElixir;
    }

    public void setStealableDarkElixir(int stealableDarkElixir) {
        this.stealableDarkElixir = stealableDarkElixir;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public List<Spell> getSpells() {
        return spells;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    public List<Hero> getHeros() {
        return heros;
    }

    public void setHeros(List<Hero> heros) {
        this.heros = heros;
    }
}
