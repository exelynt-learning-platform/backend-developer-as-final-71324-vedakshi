package com.booking.resource_booking_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        customUserDetailsService
                );

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // LOGIN
                        .requestMatchers("/auth/**")
                        .permitAll()
                        // SWAGGER
                        .requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                        )
                        .permitAll()
                        // RESOURCES
                        // USER + ADMIN can read
                        .requestMatchers(
                                HttpMethod.GET,
                                "/resources/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // Only ADMIN can create/update/delete
                        .requestMatchers(
                                HttpMethod.POST,
                                "/resources/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/resources/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/resources/**"
                        )
                        .hasRole("ADMIN")

                        
                        .requestMatchers(
                                HttpMethod.POST,
                                "/reservations"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // USER can see their own reservations
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/my"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // ADMIN can see all reservations
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations"
                        )
                        .hasRole("ADMIN")

                        // USER + ADMIN can get reservation by ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/{id}"
                        )
                        .hasAnyRole("USER", "ADMIN")
                        
                        .requestMatchers(HttpMethod.PUT, "/reservations/{id}")
                        .hasRole("ADMIN")
                        // Only ADMIN can delete
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/reservations/{id}"
                        )
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
