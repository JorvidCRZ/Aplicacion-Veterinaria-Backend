package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.ServicioResponse;
import com.PetsSecrets.Veterinaria_Backend.services.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServicioController {

    private final ServicioService servicioService;

    // Obtener todos los servicios activos
    @GetMapping
    public ResponseEntity<List<ServicioResponse>> obtenerServiciosActivos() {
        List<ServicioResponse> servicios = servicioService.obtenerServiciosActivos();
        return ResponseEntity.ok(servicios);
    }

    // Obtener servicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> obtenerServicioPorId(@PathVariable Integer id) {
        try {
            ServicioResponse servicio = servicioService.obtenerServicioPorId(id);
            return ResponseEntity.ok(servicio);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar servicios por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ServicioResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ServicioResponse> servicios = servicioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(servicios);
    }
}