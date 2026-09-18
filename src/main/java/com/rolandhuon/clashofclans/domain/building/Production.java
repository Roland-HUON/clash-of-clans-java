package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.List;

public record Production(List<Integer> perHourByLevel, ResourceType resource) {

    public static final int STORAGE_HOURS = 6;

    public Production {
        if (perHourByLevel.isEmpty()) throw new IllegalArgumentException("Production table cannot be empty");
        if (resource == null) throw new IllegalArgumentException("A producer must say what it produces");
        if (perHourByLevel.stream().anyMatch(rate -> rate <= 0)) {
            throw new IllegalArgumentException("Every production rate must be positive");
        }
        perHourByLevel = List.copyOf(perHourByLevel);
    }
}
