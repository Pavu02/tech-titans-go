package com.examly.springapp.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "https://8081-cbdcccadbb354177166deefecaaeccafbbbone.premiumproject.examly.io",
            "https://8080-cbdcccadbb354177166deefecaaeccafbbbone.premiumproject.examly.io",
            "https://ide-cbdcccadbb354177166deefecaaeccafbbbone.premiumproject.examly.io",
            "http://localhost:4200",
            "http://localhost:8081",
            "http://localhost:8080",
            "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        configuration.setExposedHeaders(Arrays.asList(
            "Authorization"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
