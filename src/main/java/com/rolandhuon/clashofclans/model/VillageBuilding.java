package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.building.Production;

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

    private static final long NANOS_PER_HOUR = 3_600_000_000_000L;

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

    public VillageBuilding(BuildingType type, int level, Instant builtAt) {
        if (level < 1 || level > type.maxLevel()) {
            throw new IllegalArgumentException(type.label() + ": level " + level + " out of bounds [1.." + type.maxLevel() + "]");
        }
        this.type = type;
        this.level = level;
        this.lastCollectedAt = type.produces() ? builtAt : null;
    }

    public int pendingProduction(Instant now) {
        if (!type.produces() || lastCollectedAt == null) return 0;

        Duration elapsed = Duration.between(lastCollectedAt, now);
        if (elapsed.isNegative() || elapsed.isZero()) return 0;

        Duration full = Duration.ofHours(Production.STORAGE_HOURS);
        if (elapsed.compareTo(full) > 0) elapsed = full;

        long produced = (long) type.productionPerHourAt(level) * elapsed.toNanos() / NANOS_PER_HOUR;
        return (int) Math.min(produced, type.mineCapacityAt(level));
    }

    public int collect(Instant now) {
        int amount = pendingProduction(now);
        if (amount == 0) return 0;

        if (amount >= type.mineCapacityAt(level)) {
            lastCollectedAt = now;
        } else {
            long nanosPaidFor = (long) amount * NANOS_PER_HOUR / type.productionPerHourAt(level);
            lastCollectedAt = lastCollectedAt.plusNanos(nanosPaidFor);
        }
        return amount;
    }


    public Long getId() { return id; }
    public BuildingType getType() { return type; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Village getVillage() { return village; }
    public void setVillage(Village village) { this.village = village; }
}
