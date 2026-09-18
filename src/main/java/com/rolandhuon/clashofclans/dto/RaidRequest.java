package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RaidRequest(@NotNull Long attackerId,
                          @NotNull Long attackerVillageId,
                          @NotNull Long targetVillageId,
                          @NotEmpty @Size(max = TroopType.MAX_ARMY_ENTRIES) List<@Valid RaidUnitRequest> army) {
}
