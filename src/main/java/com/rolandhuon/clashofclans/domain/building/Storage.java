package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.ResourceType;

import java.util.List;

public record Storage(List<Integer> capacityByLevel, ResourceType resource) {

    public Storage {
        if (capacityByLevel.isEmpty()) throw new IllegalArgumentException("Storage table cannot be empty");
        if (resource == null) throw new IllegalArgumentException("A storage must say what it holds");
        if (capacityByLevel.stream().anyMatch(capacity -> capacity <= 0)) {
            throw new IllegalArgumentException("Every storage capacity must be positive");
        }
        capacityByLevel = List.copyOf(capacityByLevel);
    }
}
