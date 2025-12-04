package com.kevdev.oms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Management Service API",
                version = "v1",
                description = "REST API for managing customers, products and orders",
                contact = @Contact(
                        name = "Kevin Thurman",
                        url = "https://github.com/kthurman59",
                        email = "noreply@example.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local dev"
                )
        }
)
public class OpenApiConfig {
}

