package com.tanmay.makemytrip_backend.common.config;

import com.tanmay.makemytrip_backend.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ==================== PASSWORD ====================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==================== AUTHENTICATION MANAGER ====================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ==================== SECURITY FILTER CHAIN ====================

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // ==================== CSRF ====================

                .csrf(csrf -> csrf.disable())

                // ==================== SESSION ====================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==================== JWT FILTER ====================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // ==================== AUTHORIZATION ====================

                .authorizeHttpRequests(auth -> auth

                        // ==================== PUBLIC ====================

                        .requestMatchers(
                                "/api/auth/login"
                        ).permitAll()

                        // Public registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).permitAll()

                        // Public flight read/search
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/flights",
                                "/api/flights/**"
                        ).permitAll()

                        // Public airline read
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/airlines",
                                "/api/airlines/**"
                        ).permitAll()

                        // Public airport read
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/airports",
                                "/api/airports/**"
                        ).permitAll()

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // ==================== ADMIN ====================

                        // Flight management
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

                        // Airline management
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

                        // Airport management
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

                        // All users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        // Deactivate user
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // ==================== USER / ADMIN ====================

                        // Individual user.
                        // UserService checks ownership.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).authenticated()

                        // Individual user update.
                        // UserService checks ownership.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()

                        // ==================== BOOKING SYSTEM ====================

                        .requestMatchers(
                                "/api/bookings/**",
                                "/api/passengers/**",
                                "/api/payments/**"
                        ).authenticated()

                        // ==================== EVERYTHING ELSE ====================

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}