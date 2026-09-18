package com.rolandhuon.clashofclans.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BattleRequest(@NotEmpty List<@Valid UnitRequest> army,
                            @NotEmpty List<@Valid BuildingRequest> village) {
}
