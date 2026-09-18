package com.rolandhuon.clashofclans.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BattleRequest(@NotEmpty @Valid List<UnitRequest> army,
                            @NotEmpty @Valid List<BuildingRequest> village) {
}
