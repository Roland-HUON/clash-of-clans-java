package com.rolandhuon.clashofclans.dto;

import com.rolandhuon.clashofclans.domain.troop.TroopType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UnitRequest(@NotBlank String type,
                          @Min(1) int level,
                          @Min(1) @Max(TroopType.MAX_ARMY_SIZE) int count) {
}
