package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.TargetScope;

import java.util.List;

public record Firepower(List<Integer> byLevel, TargetScope targets) {

    public Firepower {
        if (byLevel.isEmpty()) throw new IllegalArgumentException("Damage table cannot be empty");
        if (targets == null || targets == TargetScope.NONE) {
            throw new IllegalArgumentException("A defence must be able to shoot at something");
        }
        byLevel = List.copyOf(byLevel);
    }
}
