package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PlayerTroop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TroopType type;

    private int level;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    protected PlayerTroop(){}

    public PlayerTroop(TroopType type, int level) {
        if (level < 1 || level > type.maxLevel()) {
            throw new IllegalArgumentException(type.label() + ": level " + level + " out of bounds [1.." + type.maxLevel() + "]");
        }
        this.type = type;
        this.level = level;
    }

    public Long getId() { return id; }
    public TroopType getType() { return type; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}
