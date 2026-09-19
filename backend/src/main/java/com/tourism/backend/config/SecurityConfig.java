package com.tourism.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String JWT_SECRET =
            "change-this-secret-key-to-a-long-random-production-secret-key";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {

        SecretKey secretKey = new SecretKeySpec(
                JWT_SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKey secretKey = new SecretKeySpec(
                JWT_SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    /*
     * Converts the "role" claim from our JWT:
     *
     *     "role": "ADMIN"
     *
     * into:
     *
     *     ROLE_ADMIN
     *
     * so that hasRole("ADMIN") works.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return authenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger",
                                "/swagger/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs",
                                "/api-docs/**"
                        ).permitAll()

                        // Authentication
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // Public tourism browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/states/**",
                                "/api/destinations/**",
                                "/api/attractions/**",
                                "/api/tags/**",
                                "/api/experiences/**",
                                "/api/accommodations/**",
                                "/api/guides/**",
                                "/api/restaurants/**",
                                "/api/transport/**",
                                "/api/festivals/**",
                                "/api/festival-occurrences/**"
                        ).permitAll()

                        // Itineraries require authentication.
                        // Ownership is handled by ItineraryService.
                        .requestMatchers(
                                "/api/itineraries/**"
                        ).authenticated()

                        // Admin-only tourism data creation
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/states/**",
                                "/api/destinations/**",
                                "/api/attractions/**",
                                "/api/tags/**",
                                "/api/experiences/**",
                                "/api/accommodations/**",
                                "/api/guides/**",
                                "/api/restaurants/**",
                                "/api/transport/**",
                                "/api/festivals/**",
                                "/api/festival-occurrences/**"
                        ).hasRole("ADMIN")

                        // Admin-only tourism data updates
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/states/**",
                                "/api/destinations/**",
                                "/api/attractions/**",
                                "/api/tags/**",
                                "/api/experiences/**",
                                "/api/accommodations/**",
                                "/api/guides/**",
                                "/api/restaurants/**",
                                "/api/transport/**",
                                "/api/festivals/**",
                                "/api/festival-occurrences/**"
                        ).hasRole("ADMIN")

                        // Admin-only partial updates
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/states/**",
                                "/api/destinations/**",
                                "/api/attractions/**",
                                "/api/tags/**",
                                "/api/experiences/**",
                                "/api/accommodations/**",
                                "/api/guides/**",
                                "/api/restaurants/**",
                                "/api/transport/**",
                                "/api/festivals/**",
                                "/api/festival-occurrences/**"
                        ).hasRole("ADMIN")

                        // Admin-only deletion
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/states/**",
                                "/api/destinations/**",
                                "/api/attractions/**",
                                "/api/tags/**",
                                "/api/experiences/**",
                                "/api/accommodations/**",
                                "/api/guides/**",
                                "/api/restaurants/**",
                                "/api/transport/**",
                                "/api/festivals/**",
                                "/api/festival-occurrences/**"
                        ).hasRole("ADMIN")

                        // Everything else
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}