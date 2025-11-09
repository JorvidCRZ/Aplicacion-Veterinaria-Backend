package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.PedidoRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.PedidoResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import com.PetsSecrets.Veterinaria_Backend.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<?> obtenerPedidos() {
        try {
            List<PedidoResponse> pedidos = pedidoService.obtenerTodosLosPedidos();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> obtenerMisPedidos() {
        try {
            Integer usuarioId = obtenerUsuarioIdDelToken();
            List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
            return ResponseEntity.ok(pedidos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Integer id) {
        try {
            PedidoResponse pedido = pedidoService.obtenerPedidoPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerPedidoPorCodigo(@PathVariable String codigo) {
        try {
            PedidoResponse pedido = pedidoService.obtenerPedidoPorCodigo(codigo);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/crear-desde-carrito")
    public ResponseEntity<?> crearPedidoDesdeCarrito(@RequestBody PedidoRequest request) {
        try {
            // Validaciones básicas
            if (request.getDireccion() == null || request.getDireccion().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La dirección es requerida"));
            }
            if (request.getTelefonoContacto() == null || request.getTelefonoContacto().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El teléfono de contacto es requerido"));
            }
            if (request.getMetodoPago() == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El método de pago es requerido"));
            }

            Integer usuarioId = obtenerUsuarioIdDelToken();
            PedidoResponse pedido = pedidoService.crearPedidoDesdeCarrito(usuarioId, request);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Integer id, 
                                                   @RequestParam String estado) {
        try {
            PedidoResponse pedido = pedidoService.actualizarEstadoPedido(id, estado);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorEstado(@PathVariable String estado) {
        try {
            List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
            return ResponseEntity.ok(pedidos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/{id}/fecha-entrega")
    public ResponseEntity<?> actualizarFechaEntrega(@PathVariable Integer id, 
                                                   @RequestParam String fechaEntrega) {
        try {
            PedidoResponse pedido = pedidoService.actualizarFechaEntrega(id, fechaEntrega);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
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
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        // El username en el token es el email del usuario
        String email = authentication.getName();
        
        // Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        
        return usuario.getId();
    }
}