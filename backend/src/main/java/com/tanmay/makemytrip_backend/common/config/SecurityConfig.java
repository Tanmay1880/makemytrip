package com.tanmay.makemytrip_backend.common.config;

import com.tanmay.makemytrip_backend.security.jwt.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ============================================================
    // PASSWORD
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // AUTHENTICATION MANAGER
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ============================================================
    // CORS CONFIGURATION
    // ============================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5174",
                        "http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // ============================================================
    // CORS FILTER
    // ============================================================

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {

        CorsFilter corsFilter =
                new CorsFilter(corsConfigurationSource());

        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(corsFilter);

        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }

    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ====================================================
                // CSRF
                // ====================================================

                .csrf(csrf -> csrf.disable())

                // ====================================================
                // CORS
                // ====================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // ====================================================
                // SESSION
                // ====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ====================================================
                // JWT FILTER
                // ====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // ====================================================
                // AUTHORIZATION
                // ====================================================

                .authorizeHttpRequests(auth -> auth

                        // ------------------------------------------------
                        // PUBLIC AUTHENTICATION
                        // ------------------------------------------------

                        .requestMatchers(
                                "/api/auth/login"
                        ).permitAll()

                        // ------------------------------------------------
                        // PUBLIC REGISTRATION
                        // ------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).permitAll()

                        // ------------------------------------------------
                        // PUBLIC FLIGHT READ
                        // ------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/flights",
                                "/api/flights/**"
                        ).permitAll()

                        // ------------------------------------------------
                        // PUBLIC AIRLINE READ
                        // ------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/airlines",
                                "/api/airlines/**"
                        ).permitAll()

                        // ------------------------------------------------
                        // PUBLIC AIRPORT READ
                        // ------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/airports",
                                "/api/airports/**"
                        ).permitAll()

                        // ------------------------------------------------
                        // CORS PREFLIGHT
                        // ------------------------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // =================================================
                        // ADMIN - FLIGHTS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/flights"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/flights/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/flights/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // ADMIN - AIRLINES
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/airlines"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/airlines/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/airlines/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // ADMIN - AIRPORTS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/airports"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/airports/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/airports/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // ADMIN - USERS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // USER / ADMIN - INDIVIDUAL USER
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()

                        // =================================================
                        // BOOKING SYSTEM
                        // =================================================

                        .requestMatchers(
                                "/api/bookings/**",
                                "/api/passengers/**",
                                "/api/payments/**"
                        ).authenticated()

                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}