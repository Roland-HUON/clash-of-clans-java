package com.rolandhuon.clashofclans.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class GameConfiguration {
    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }
}
