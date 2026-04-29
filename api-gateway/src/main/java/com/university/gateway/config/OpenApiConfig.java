package com.university.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway-level OpenAPI configuration.
 *
 * Defines the bearerAuth security scheme that is shared across all service
 * specs served via SwaggerProxyController.  The "Authorize" button appears
 * in Swagger UI — paste the JWT token obtained from
 *   POST /identity/auth/login
 * and every "Try it out" request will include it automatically as:
 *   Authorization: Bearer <token>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("University Training Portal — API Gateway")
                        .description(
                                "Aggregated API documentation for all microservices.\n\n" +
                                "**How to authenticate:**\n" +
                                "1. Call `POST /identity/auth/login` with your credentials.\n" +
                                "2. Copy the `token` from the response.\n" +
                                "3. Click the **Authorize** button and paste the token.\n\n" +
                                "All requests are proxied through the API Gateway on port **3000**.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("University Training Portal")
                                .email("admin@university.com")))
                // Register bearerAuth scheme so Swagger UI shows the Authorize button
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "JWT token from POST /identity/auth/login. " +
                                                "Enter only the token value — do NOT include 'Bearer'.")))
                // Apply bearerAuth globally to all endpoints in this spec
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
