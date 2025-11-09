package com.PetsSecrets.Veterinaria_Backend.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // Establecer la zona horaria por defecto para toda la aplicación
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }
}