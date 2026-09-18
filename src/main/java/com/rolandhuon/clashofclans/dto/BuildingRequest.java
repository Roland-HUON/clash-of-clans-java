package com.rolandhuon.clashofclans.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BuildingRequest(@NotBlank String type, @Min(1) int level) {
}
