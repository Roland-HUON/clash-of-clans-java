package com.rolandhuon.clashofclans.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class BattleRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant playedAt;
    private int destructionPercentage, stars, turns, survivingTroops;

    protected BattleRecord(){}

    public BattleRecord(Instant playedAt, int destructionPercentage, int stars, int turns, int survivingTroops) {
        this.playedAt = playedAt;
        this.destructionPercentage = destructionPercentage;
        this.stars = stars;
        this.turns = turns;
        this.survivingTroops = survivingTroops;
    }

    public Long getId() {
        return id;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

    public int getDestructionPercentage() {
        return destructionPercentage;
    }

    public int getStars() {
        return stars;
    }

    public int getTurns() {
        return turns;
    }

    public int getSurvivingTroops() {
        return survivingTroops;
    }
}
