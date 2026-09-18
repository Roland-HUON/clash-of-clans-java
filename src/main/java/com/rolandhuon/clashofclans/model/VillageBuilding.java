package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.building.BuildingType;

import java.time.Duration;
import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class VillageBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        @Enumerated(EnumType.STRING)
    private BuildingType type;

    private int level;

    private Instant lastCollectedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "village_id")
    private Village village;

    protected VillageBuilding(){}

    public VillageBuilding(BuildingType type, int level) {
        if (level < 1 || level > type.maxLevel()) {
            throw new IllegalArgumentException(type.label() + ": level " + level + " out of bounds [1.." + type.maxLevel() + "]");
        }
        this.type = type;
        this.level = level;
        this.lastCollectedAt = type.produces() ? Instant.now() : null;
    }

    public int pendingProduction(Instant now) {
        if (!type.produces() || lastCollectedAt == null) return 0;

        long minutes = Duration.between(lastCollectedAt, now).toMinutes();
        if (minutes <= 0) return 0;

        long produced = (long) type.productionPerHourAt(level) * minutes / 60;
        return (int) Math.min(produced, type.storageCapacityAt(level));
    }

    public int collect(Instant now) {
        int amount = pendingProduction(now);
        if (type.produces()) lastCollectedAt = now;
        return amount;
    }

    public void startProducing(Instant now) {
        if (type.produces() && lastCollectedAt == null) lastCollectedAt = now;
    }

    public Long getId() { return id; }
    public BuildingType getType() { return type; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Village getVillage() { return village; }
    public void setVillage(Village village) { this.village = village; }
}
