package com.rolandhuon.clashofclans.dto;

import java.util.List;

public record RaidRequest(Long attackerId, Long targetVillageId, List<RaidUnitRequest> army) {
}
