package com.codewithkelvin.store.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI is the front door of the deployed demo, so it carries the demo
 * credentials. A reviewer should be able to log in and call a protected endpoint
 * without reading the source first.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI storeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Store API")
                        .version("1.0.0")
                        .description("""
                                E-commerce backend: catalogue, anonymous carts, orders and Stripe checkout.

                                **Try it:** POST /auth/login with `demo@store.dev` / `Demo1234!` \
                                (or `admin@store.dev` / `Admin1234!` for catalogue writes), then paste the \
                                returned token into **Authorize** above.

                                Payments run in Stripe test mode: card `4242 4242 4242 4242`, any future \
                                expiry, any CVC. No real money moves.
                                """))
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .name(BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
