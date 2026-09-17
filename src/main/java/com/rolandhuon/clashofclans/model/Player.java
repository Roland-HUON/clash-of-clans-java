package com.rolandhuon.clashofclans.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level, gold, elixir, darkElixir, trophies;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Village> villages = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerTroop> troops = new ArrayList<>();

    protected Player(){}

    public Player(String name, int level, int gold, int elixir, int darkElixir) {
        this(name, level, gold, elixir, darkElixir, 0);
    }

    public Player(String name, int level, int gold, int elixir, int darkElixir, int trophies) {
        this.name = name;
        this.level = level;
        this.gold = gold;
        this.elixir = elixir;
        this.darkElixir = darkElixir;
        this.trophies = trophies;
    }

    public void addVillage(Village village) {
        villages.add(village);
        village.setPlayer(this);
    }

    public void addTroop(PlayerTroop troop) {
        troops.add(troop);
        troop.setPlayer(this);
    }

    public void spendGold(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be >= 0");
        if (gold < amount) throw new IllegalStateException("Not enough gold: " + gold + " < " + amount);
        gold -= amount;
    }

    public void spendElixir(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be >= 0");
        if (elixir < amount) throw new IllegalStateException("Not enough elixir: " + elixir + " < " + amount);
        elixir -= amount;
    }

    public void earn(int gold, int elixir, int darkElixir) {
        if (gold < 0 || elixir < 0 || darkElixir < 0) throw new IllegalArgumentException("Loot must be >= 0");
        this.gold += gold;
        this.elixir += elixir;
        this.darkElixir += darkElixir;
    }

    public void applyTrophyDelta(int delta) {
        this.trophies = Math.max(0, this.trophies + delta);
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getElixir() { return elixir; }
    public void setElixir(int elixir) { this.elixir = elixir; }

    public int getDarkElixir() { return darkElixir; }
    public void setDarkElixir(int darkElixir) { this.darkElixir = darkElixir; }

    public int getTrophies() { return trophies; }
    public void setTrophies(int trophies) { this.trophies = trophies; }

    public List<Village> getVillages() { return villages; }
    public List<PlayerTroop> getTroops() { return troops; }
}
