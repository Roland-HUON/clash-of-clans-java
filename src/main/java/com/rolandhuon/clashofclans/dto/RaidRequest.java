package com.rolandhuon.clashofclans.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RaidRequest(@NotNull Long attackerId,
                          @NotNull Long attackerVillageId,
                          @NotNull Long targetVillageId,
                          @NotEmpty List<@Valid RaidUnitRequest> army) {
}
