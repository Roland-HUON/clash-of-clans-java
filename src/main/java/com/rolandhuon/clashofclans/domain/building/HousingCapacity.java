package com.rolandhuon.clashofclans.domain.building;

import java.util.List;

public record HousingCapacity(List<Integer> byLevel) {

    public HousingCapacity {
        if (byLevel.isEmpty()) throw new IllegalArgumentException("Capacity table cannot be empty");
        byLevel = List.copyOf(byLevel);
    }
}
