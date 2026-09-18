package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BattleRequest(@NotEmpty @Size(max = TroopType.MAX_ARMY_ENTRIES) List<@Valid UnitRequest> army,
                            @NotEmpty List<@Valid BuildingRequest> village) {
}
