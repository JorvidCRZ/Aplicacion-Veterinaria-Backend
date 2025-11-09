package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.EstadisticasResponse;
import com.PetsSecrets.Veterinaria_Backend.services.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EstadisticasController {
    
    private final EstadisticasService estadisticasService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticasDashboard() {
        EstadisticasResponse estadisticas = estadisticasService.obtenerEstadisticasDashboard();
        return ResponseEntity.ok(estadisticas);
    }
    
    @GetMapping("/reporte")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadisticasResponse> obtenerReporte(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        EstadisticasResponse reporte = estadisticasService.obtenerReporte(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}