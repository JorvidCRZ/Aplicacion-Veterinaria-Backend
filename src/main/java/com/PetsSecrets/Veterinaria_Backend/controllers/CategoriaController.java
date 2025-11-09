package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.services.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> obtenerTodasLasCategorias() {
        try {
            List<CategoriaResponse> categorias = categoriaService.obtenerTodasLasCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerCategoriaPorId(@PathVariable Integer id) {
        try {
            CategoriaResponse categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El nombre de la categoría es requerido"));
            }

            CategoriaResponse categoria = categoriaService.crearCategoria(request);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Integer id, 
                                                @RequestBody CategoriaRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El nombre de la categoría es requerido"));
            }

            CategoriaResponse categoria = categoriaService.actualizarCategoria(id, request);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminarCategoria(@PathVariable Integer id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.ok(new MessageResponse("Categoría eliminada exitosamente"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponse>> buscarCategoriasPorNombre(
            @RequestParam String nombre) {
        try {
            List<CategoriaResponse> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}