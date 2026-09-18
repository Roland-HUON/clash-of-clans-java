package com.rolandhuon.clashofclans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "coc.api.rate-limit")
public record RateLimitProperties(@DefaultValue("true") boolean enabled,
                                  @DefaultValue("60") int capacity,
                                  @DefaultValue("60") int refillSeconds,
                                  @DefaultValue("10000") int maxTrackedClients,
                                  @DefaultValue("false") boolean trustForwardedFor) {
}
