package com.tourism.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
                        .version("1.0.0")
                        .description("""
                                REST API for the India Tourism Platform.

                                This platform helps users discover and plan travel across India by providing information about:
                                • States
                                • Destinations
                                • Festivals
                                • Experiences
                                • Hotels
                                • Tour Guides
                                • Transport

                                Built using Spring Boot, PostgreSQL, and React.
                                """)

                        .contact(new Contact()
                                .name("Anurag Kakoty")
                                .email("anuragkakoty@gmail.com"))

                        .license(new License()
                                .name("MIT License")))

                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/Anurag-Kakoty/tourism-app"));
    }
}