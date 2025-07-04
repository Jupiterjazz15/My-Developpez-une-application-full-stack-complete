package com.openclassrooms.mddapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permet toutes les routes et autorise les requêtes de localhost:4200
        registry.addMapping("/**") // Autoriser toutes les routes de l'API
                .allowedOrigins("http://localhost:4200")  // Permet les requêtes depuis le frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Les méthodes HTTP autorisées
                .allowedHeaders("*");  // Permet tous les en-têtes
    }
}
