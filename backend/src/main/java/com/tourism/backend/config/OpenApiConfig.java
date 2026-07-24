package com.tourism.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tourismOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("India Tourism Platform API")
                        .description("""
                                REST API for the India Tourism Platform.
                                
                                This API provides endpoints for managing:
                                - States
                                - Destinations
                                - Festivals
                                - Experiences
                                - Hotels
                                - Guides
                                - Transport
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Anurag Kakoty")
                                .email("anuragkakoty@gmail.com"))
                        .license(new License()
                                .name("MIT License")));
    }

}