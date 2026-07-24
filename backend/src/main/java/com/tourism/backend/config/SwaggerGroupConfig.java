package com.tourism.backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupConfig {

    @Bean
    public GroupedOpenApi stateApi() {
        return GroupedOpenApi.builder()
                .group("States")
                .pathsToMatch("/api/states/**")
                .build();
    }

}