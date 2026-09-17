package com.rolandhuon.clashofclans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "coc.battle")
public record BattleProperties(@DefaultValue("180") int maxTurns,
                               @DefaultValue("352") int maxHousingSpace) {

    public BattleProperties {
        if (maxTurns < 1) throw new IllegalArgumentException("coc.battle.max-turns must be >= 1");
        if (maxHousingSpace < 1) throw new IllegalArgumentException("coc.battle.max-housing-space must be >= 1");
    }
}
