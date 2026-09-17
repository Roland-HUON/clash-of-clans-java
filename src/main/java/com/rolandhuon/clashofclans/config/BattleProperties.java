package com.rolandhuon.clashofclans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coc.battle")
public record BattleProperties(int maxTurns) {
}
