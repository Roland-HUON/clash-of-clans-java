package com.rolandhuon.clashofclans.service;

import com.rolandhuon.clashofclans.domain.battle.BattleResult;
import com.rolandhuon.clashofclans.domain.battle.Loot;
import com.rolandhuon.clashofclans.domain.battle.TrophyExchange;

public record RaidOutcome(BattleResult battle, Loot loot, TrophyExchange trophies,
                          int attackerTrophies, int defenderTrophies) {
}
