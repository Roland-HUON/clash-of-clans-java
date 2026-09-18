package com.rolandhuon.clashofclans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "coc.api.rate-limit")
public record RateLimitProperties(@DefaultValue("true") boolean enabled,
                                  @DefaultValue("60") int capacity,
                                  @DefaultValue("60") int refillSeconds,
                                  @DefaultValue("10000") int maxTrackedClients,
                                  @DefaultValue("false") boolean trustForwardedFor) {

    public RateLimitProperties {
        if (capacity < 1) throw new IllegalArgumentException("coc.api.rate-limit.capacity must be >= 1");
        if (refillSeconds < 1) throw new IllegalArgumentException("coc.api.rate-limit.refill-seconds must be >= 1");
        if (maxTrackedClients < 1) throw new IllegalArgumentException("coc.api.rate-limit.max-tracked-clients must be >= 1");
    }
}
