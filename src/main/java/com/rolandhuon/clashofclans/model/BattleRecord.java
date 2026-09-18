package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.battle.BattleKind;

import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private BattleKind kind;
    private int destructionPercentage, stars, turns, survivingTroops;

    protected BattleRecord(){}

    public BattleRecord(BattleKind kind, Instant playedAt, int destructionPercentage, int stars, int turns, int survivingTroops) {
        this.kind = kind;
        this.playedAt = playedAt;
        this.destructionPercentage = destructionPercentage;
        this.stars = stars;
        this.turns = turns;
        this.survivingTroops = survivingTroops;
    }

    public Long getId() {
        return id;
    }

    public BattleKind getKind() {
        return kind;
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
