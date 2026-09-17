package com.rolandhuon.clashofclans.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int gold, elixir, darkElixir;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VillageBuilding> buildings = new ArrayList<>();

    protected Village(){}

    public Village(String name, int gold, int elixir, int darkElixir) {
        this.name = name;
        this.gold = gold;
        this.elixir = elixir;
        this.darkElixir = darkElixir;
    }

    public void addBuilding(VillageBuilding building) {
        buildings.add(building);
        building.setVillage(this);
    }

    public void loot(int gold, int elixir, int darkElixir) {
        this.gold = Math.max(0, this.gold - gold);
        this.elixir = Math.max(0, this.elixir - elixir);
        this.darkElixir = Math.max(0, this.darkElixir - darkElixir);
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getElixir() { return elixir; }
    public void setElixir(int elixir) { this.elixir = elixir; }

    public int getDarkElixir() { return darkElixir; }
    public void setDarkElixir(int darkElixir) { this.darkElixir = darkElixir; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public List<VillageBuilding> getBuildings() { return buildings; }
}
