package com.realtors.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Add allowed origins, headers, and methods
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("Content-Type");
//        config.addAllowedHeader("Authorization");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("OPTIONS");
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);  // Apply CORS configuration to the API endpoints
        return new CorsFilter(source);
    }
}
