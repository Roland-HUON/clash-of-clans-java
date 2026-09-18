package com.rolandhuon.clashofclans.domain.building;

import com.rolandhuon.clashofclans.domain.common.AttackProfile;
import com.rolandhuon.clashofclans.domain.common.TargetScope;

import java.util.List;

public record Firepower(List<Integer> byLevel, TargetScope targets, AttackProfile profile) {

    public Firepower(List<Integer> byLevel, TargetScope targets) {
        this(byLevel, targets, AttackProfile.single(0));
    }

    public Firepower {
        if (byLevel.isEmpty()) throw new IllegalArgumentException("Damage table cannot be empty");
        if (targets == null || targets == TargetScope.NONE) {
            throw new IllegalArgumentException("A defence must be able to shoot at something");
        }
        if (profile == null) throw new IllegalArgumentException("A defence must say how it hits");
        byLevel = List.copyOf(byLevel);
    }
}
