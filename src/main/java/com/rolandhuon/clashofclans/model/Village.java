package com.rolandhuon.clashofclans.model;

import com.rolandhuon.clashofclans.domain.building.BuildingType;
import com.rolandhuon.clashofclans.domain.common.Balance;
import com.rolandhuon.clashofclans.domain.common.ResourceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long gold, elixir, darkElixir;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<VillageBuilding> buildings = new ArrayList<>();

    protected Village(){}

    public Village(String name, long gold, long elixir, long darkElixir) {
        this.name = name;
        this.gold = gold;
        this.elixir = elixir;
        this.darkElixir = darkElixir;
    }

    public boolean hasRoomFor(BuildingType type) {
        return buildings.stream().filter(b -> b.getType() == type).count() < type.maxCount();
    }

    public void addBuilding(VillageBuilding building) {
        BuildingType type = building.getType();

        if (!hasRoomFor(type)) {
            throw new IllegalStateException("Too many " + type.label() + " (max " + type.maxCount() + ")");
        }

        buildings.add(building);
        building.setVillage(this);
    }

    public void stock(long gold, long elixir, long darkElixir) {
        this.gold = capped(this.gold, gold, ResourceType.GOLD);
        this.elixir = capped(this.elixir, elixir, ResourceType.ELIXIR);
        this.darkElixir = capped(this.darkElixir, darkElixir, ResourceType.DARK_ELIXIR);
    }

    public long capacityFor(ResourceType resource) {
        return buildings.stream()
                .filter(building -> building.getType().stores())
                .filter(building -> building.getType().storedResource() == resource)
                .mapToLong(building -> building.getType().storageCapacityAt(building.getLevel()))
                .sum();
    }

    private long capped(long current, long added, ResourceType resource) {
        long raised = Balance.plus(current, added);
        long capacity = capacityFor(resource);

        return capacity == 0 ? raised : Math.min(raised, Math.max(capacity, current));
    }

    public void loot(long gold, long elixir, long darkElixir) {
        this.gold = Math.max(0, this.gold - gold);
        this.elixir = Math.max(0, this.elixir - elixir);
        this.darkElixir = Math.max(0, this.darkElixir - darkElixir);
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getGold() { return gold; }
    public void setGold(long gold) { this.gold = gold; }

    public long getElixir() { return elixir; }
    public void setElixir(long elixir) { this.elixir = elixir; }

    public long getDarkElixir() { return darkElixir; }
    public void setDarkElixir(long darkElixir) { this.darkElixir = darkElixir; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public List<VillageBuilding> getBuildings() { return buildings; }
}
