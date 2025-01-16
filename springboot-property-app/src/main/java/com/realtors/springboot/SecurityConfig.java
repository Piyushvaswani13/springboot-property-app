package com.realtors.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection if needed
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // Allow preflight requests
                                .anyRequest().permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only routes
//                        .requestMatchers("/builder/**").hasRole("BUILDER") // Builder-only routes
//                        .requestMatchers("/customer/**").hasRole("CUSTOMER") // Customer-only routes
//                        .anyRequest().permitAll() // All other routes require authentication
                )
                .cors(cors -> cors.configurationSource(request -> new org.springframework.web.cors.CorsConfiguration().applyPermitDefaultValues()));

//                .formLogin(form -> form
//               .loginPage("/login")
//                       .defaultSuccessUrl("/dashboard", true) // Redirect after successful login
//                           .loginProcessingUrl("/api/auth/login")
//                                .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
