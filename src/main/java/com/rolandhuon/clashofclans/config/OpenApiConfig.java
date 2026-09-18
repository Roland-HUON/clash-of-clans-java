package com.rolandhuon.clashofclans.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clashOfClansOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Clash of Clans API")
                .version("0.0.1")
                .description("""
                        Game engine exposed over HTTP.

                        Catalog: troop and building types with their stats per level.
                        Players: CRUD, villages, researched troops, trophies.
                        Gameplay: upgrade buildings and troops, raid other villages, leaderboard.
                        """)
                .contact(new Contact().name("Roland Huon"))
                .license(new License().name("School project")));
    }
}
