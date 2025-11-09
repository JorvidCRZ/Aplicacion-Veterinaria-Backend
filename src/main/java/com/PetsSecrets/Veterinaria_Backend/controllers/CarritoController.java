package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.CarritoItemRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.CarritoResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import com.PetsSecrets.Veterinaria_Backend.services.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    // Endpoint de prueba para verificar autenticación
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        System.out.println("=== TEST AUTH ENDPOINT ===");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            System.out.println("Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "null"));
            
            if (authentication != null) {
                System.out.println("Principal: " + authentication.getPrincipal());
                System.out.println("Name: " + authentication.getName());
                System.out.println("Authorities: " + authentication.getAuthorities());
            }
            
            Integer usuarioId = obtenerUsuarioIdDelToken();
            return ResponseEntity.ok(new MessageResponse("Usuario autenticado correctamente. ID: " + usuarioId));
        } catch (Exception e) {
            System.err.println("Error en test-auth: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(403).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        try {
            Integer usuarioId = obtenerUsuarioIdDelToken();
            CarritoResponse carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody CarritoItemRequest request) {
        System.out.println("=== ENDPOINT AGREGAR PRODUCTO ===");
        System.out.println("Request recibido: " + request);
        System.out.println("ProductoId: " + request.getProductoId());
        System.out.println("Cantidad: " + request.getCantidad());
        
        try {
            // Validaciones básicas
            if (request.getProductoId() == null) {
                System.err.println("ERROR: ProductoId es null");
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El ID del producto es requerido"));
            }
            if (request.getCantidad() == null || request.getCantidad() <= 0) {
                System.err.println("ERROR: Cantidad inválida: " + request.getCantidad());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La cantidad debe ser mayor a 0"));
            }

            System.out.println("Validaciones pasadas, obteniendo usuario...");
            Integer usuarioId = obtenerUsuarioIdDelToken();
            
            System.out.println("Llamando al servicio con usuarioId: " + usuarioId + ", request: " + request);
            CarritoResponse carrito = carritoService.agregarProductoAlCarrito(usuarioId, request);
            
            System.out.println("SUCCESS: Carrito actualizado: " + carrito);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            System.err.println("ERROR RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("ERROR Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Integer itemId, 
                                               @RequestParam Integer cantidad) {
        try {
            if (cantidad < 0) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La cantidad no puede ser negativa"));
            }

            Integer usuarioId = obtenerUsuarioIdDelToken();
            CarritoResponse carrito = carritoService.actualizarCantidadItem(usuarioId, itemId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Integer itemId) {
        try {
            Integer usuarioId = obtenerUsuarioIdDelToken();
            CarritoResponse carrito = carritoService.eliminarItemDelCarrito(usuarioId, itemId);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<MessageResponse> vaciarCarrito() {
        try {
            Integer usuarioId = obtenerUsuarioIdDelToken();
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.ok(new MessageResponse("Carrito vaciado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/producto/{productoId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer productoId) {
        try {
            Integer usuarioId = obtenerUsuarioIdDelToken();
            CarritoResponse carrito = carritoService.eliminarProductoDelCarrito(usuarioId, productoId);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    // Método auxiliar para obtener el ID del usuario del token JWT
    private Integer obtenerUsuarioIdDelToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return usuario.getId();
        }
        
        throw new RuntimeException("Usuario no autenticado");
    }
}