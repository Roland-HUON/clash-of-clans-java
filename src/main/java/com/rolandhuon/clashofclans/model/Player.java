package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.common.Balance;
import com.rolandhuon.clashofclans.domain.common.ResourceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level, trophies;
    private long gold, elixir, darkElixir;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Village> villages = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<PlayerTroop> troops = new ArrayList<>();

    protected Player(){}

    public Player(String name, int level, long gold, long elixir, long darkElixir) {
        this(name, level, gold, elixir, darkElixir, 0);
    }

    public Player(String name, int level, long gold, long elixir, long darkElixir, int trophies) {
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

    public long balanceOf(ResourceType resource) {
        return switch (resource) {
            case GOLD -> gold;
            case ELIXIR -> elixir;
            case DARK_ELIXIR -> darkElixir;
        };
    }

    public void spend(ResourceType resource, long amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be >= 0");

        long balance = balanceOf(resource);
        if (balance < amount) {
            throw new IllegalStateException("Not enough " + resource + ": " + balance + " < " + amount);
        }

        switch (resource) {
            case GOLD -> gold -= amount;
            case ELIXIR -> elixir -= amount;
            case DARK_ELIXIR -> darkElixir -= amount;
        }
    }

    public void earn(long gold, long elixir, long darkElixir) {
        if (gold < 0 || elixir < 0 || darkElixir < 0) throw new IllegalArgumentException("Loot must be >= 0");
        this.gold = Balance.plus(this.gold, gold);
        this.elixir = Balance.plus(this.elixir, elixir);
        this.darkElixir = Balance.plus(this.darkElixir, darkElixir);
    }

    public void applyTrophyDelta(int delta) {
        this.trophies = Math.max(0, this.trophies + delta);
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getGold() { return gold; }
    public void setGold(long gold) { this.gold = gold; }

    public long getElixir() { return elixir; }
    public void setElixir(long elixir) { this.elixir = elixir; }

    public long getDarkElixir() { return darkElixir; }
    public void setDarkElixir(long darkElixir) { this.darkElixir = darkElixir; }

    public int getTrophies() { return trophies; }
    public void setTrophies(int trophies) { this.trophies = trophies; }

    public List<Village> getVillages() { return villages; }
    public List<PlayerTroop> getTroops() { return troops; }
}
