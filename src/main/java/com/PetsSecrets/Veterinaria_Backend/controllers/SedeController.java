package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.SedeResponse;
import com.PetsSecrets.Veterinaria_Backend.services.SedeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SedeController {

    private final SedeService sedeService;

    // Obtener todas las sedes
    @GetMapping
    public ResponseEntity<List<SedeResponse>> obtenerTodasLasSedes() {
        List<SedeResponse> sedes = sedeService.obtenerTodasLasSedes();
        return ResponseEntity.ok(sedes);
    }

    // Obtener sede por ID
    @GetMapping("/{id}")
    public ResponseEntity<SedeResponse> obtenerSedePorId(@PathVariable Integer id) {
        try {
            SedeResponse sede = sedeService.obtenerSedePorId(id);
            return ResponseEntity.ok(sede);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar sedes por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<SedeResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<SedeResponse> sedes = sedeService.buscarPorNombre(nombre);
        return ResponseEntity.ok(sedes);
    }

    // Buscar sedes por ciudad
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<SedeResponse>> buscarPorCiudad(@PathVariable String ciudad) {
        List<SedeResponse> sedes = sedeService.buscarPorCiudad(ciudad);
        return ResponseEntity.ok(sedes);
    }
}