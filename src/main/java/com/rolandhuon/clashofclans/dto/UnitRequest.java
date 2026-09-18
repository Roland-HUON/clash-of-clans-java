package com.rolandhuon.clashofclans.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UnitRequest(@NotBlank String type, @Min(1) int level, @Min(1) int count) {
}
