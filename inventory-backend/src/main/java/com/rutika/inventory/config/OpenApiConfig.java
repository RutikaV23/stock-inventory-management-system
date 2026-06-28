package com.rutika.inventory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Inventory Management System API")
                        .description("RESTful API for managing products, stock-in, and stock-out transactions in an inventory system. Provides endpoints for CRUD operations, stock history, dashboard statistics, and Excel export.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rutika")
                                .email("rutika@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")))
                .tags(List.of(
                        new Tag().name("Authentication").description("Authentication and user profile management endpoints"),
                        new Tag().name("User Management").description("User management endpoints for admin users"),
                        new Tag().name("Products").description("Product management endpoints for CRUD operations and Excel export"),
                        new Tag().name("Stock").description("Stock transaction endpoints for stock-in, stock-out, and history"),
                        new Tag().name("Dashboard").description("Dashboard statistics endpoints")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token-based authentication")));
    }
}
