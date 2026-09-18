package com.rolandhuon.clashofclans.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI clashOfClansOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("user / password, or admin / admin to delete.")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH))
                .info(new Info()
                        .title("Clash of Clans API")
                        .version("0.0.1")
                        .description("""
                                Game engine exposed over HTTP.

                                Catalog: troop and building types with their stats per level.
                                Players: CRUD, villages, researched troops, trophies.
                                Gameplay: upgrade buildings and troops, raid other villages, leaderboard.

                                Press Authorize and sign in as user / password before trying an
                                operation out. Deleting needs admin / admin.
                                """)
                        .contact(new Contact().name("Roland Huon"))
                        .license(new License().name("School project")));
    }
}
