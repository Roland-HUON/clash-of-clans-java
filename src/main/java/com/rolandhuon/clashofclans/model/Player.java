package com.rolandhuon.clashofclans.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level, gold, elixir, darkElixir;

    protected  Player(){}

    public Player(String name, int level, int gold, int elixir, int darkElixir) {
        this.name = name;
        this.level = level;
        this.gold = gold;
        this.elixir = elixir;
        this.darkElixir = darkElixir;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getElixir() {
        return elixir;
    }

    public void setElixir(int elixir) {
        this.elixir = elixir;
    }

    public int getDarkElixir() {
        return darkElixir;
    }

    public void setDarkElixir(int darkElixir) {
        this.darkElixir = darkElixir;
    }
}
