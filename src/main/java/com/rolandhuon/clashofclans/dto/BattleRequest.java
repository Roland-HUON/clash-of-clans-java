package com.rolandhuon.clashofclans.dto;

import java.util.List;

public record BattleRequest(List<UnitRequest> army, List<BuildingRequest> village) {
}
