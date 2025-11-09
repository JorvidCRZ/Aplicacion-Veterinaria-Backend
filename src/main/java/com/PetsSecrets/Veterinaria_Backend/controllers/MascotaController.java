package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.MascotaRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.MascotaResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import com.PetsSecrets.Veterinaria_Backend.services.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioRepository usuarioRepository;

    // Crear nueva mascota (requiere autenticación)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearMascota(@Valid @RequestBody MascotaRequest request, 
                                         Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            MascotaResponse mascota = mascotaService.crearMascota(request, usuarioId);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear mascota: " + e.getMessage()));
        }
    }

    // Obtener todas las mascotas activas
    @GetMapping
    public ResponseEntity<List<MascotaResponse>> obtenerTodasLasMascotas() {
        List<MascotaResponse> mascotas = mascotaService.obtenerTodasLasMascotas();
        return ResponseEntity.ok(mascotas);
    }

    // Obtener mascotas disponibles para adopción
    @GetMapping("/disponibles")
    public ResponseEntity<List<MascotaResponse>> obtenerMascotasDisponibles() {
        List<MascotaResponse> mascotas = mascotaService.obtenerMascotasDisponibles();
        return ResponseEntity.ok(mascotas);
    }

    // Obtener mascota por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMascotaPorId(@PathVariable Integer id) {
        try {
            MascotaResponse mascota = mascotaService.obtenerMascotaPorId(id);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener mascota: " + e.getMessage()));
        }
    }

    // Obtener mis mascotas (requiere autenticación)
    @GetMapping("/mis-mascotas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<MascotaResponse>> obtenerMisMascotas(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer usuarioId = getUserIdFromUserDetails(userDetails);
        
        List<MascotaResponse> mascotas = mascotaService.obtenerMascotasPorUsuario(usuarioId);
        return ResponseEntity.ok(mascotas);
    }

    // Buscar mascotas por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<MascotaResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<MascotaResponse> mascotas = mascotaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(mascotas);
    }

    // Buscar mascotas con filtros
    @GetMapping("/filtrar")
    public ResponseEntity<List<MascotaResponse>> buscarConFiltros(
            @RequestParam(required = false) Mascota.Especie especie,
            @RequestParam(required = false) Mascota.Tamano tamano,
            @RequestParam(required = false) Mascota.Genero genero,
            @RequestParam(required = false) Mascota.EstadoAdopcion estadoAdopcion,
            @RequestParam(required = false) Mascota.Tipo tipo,
            @RequestParam(required = false) Boolean vacunado,
            @RequestParam(required = false) Boolean esterilizado) {
        
        List<MascotaResponse> mascotas = mascotaService.buscarMascotasConFiltros(
                especie, tamano, genero, estadoAdopcion, tipo, vacunado, esterilizado);
        return ResponseEntity.ok(mascotas);
    }

    // Actualizar mascota (requiere autenticación y ser propietario o admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> actualizarMascota(@PathVariable Integer id,
                                              @Valid @RequestBody MascotaRequest request,
                                              Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            MascotaResponse mascota = mascotaService.actualizarMascota(id, request, usuarioId);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar mascota: " + e.getMessage()));
        }
    }

    // Cambiar estado de adopción (solo admin)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstadoAdopcion(@PathVariable Integer id,
                                                  @RequestParam Mascota.EstadoAdopcion estado) {
        try {
            MascotaResponse mascota = mascotaService.cambiarEstadoAdopcion(id, estado);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cambiar estado: " + e.getMessage()));
        }
    }

    // Eliminar mascota (soft delete - requiere ser propietario o admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> eliminarMascota(@PathVariable Integer id,
                                           Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            mascotaService.eliminarMascota(id, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Mascota eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar mascota: " + e.getMessage()));
        }
    }

    // Obtener estadísticas (solo admin)
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MascotaService.MascotaEstadisticas> obtenerEstadisticas() {
        MascotaService.MascotaEstadisticas estadisticas = mascotaService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    // Método auxiliar para obtener el ID del usuario del token JWT
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        return obtenerUsuarioIdDelToken();
    }
    
    private Integer obtenerUsuarioIdDelToken() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        // El username en el token es el email del usuario
        String email = authentication.getName();
        
        // Buscar el usuario por email
        com.PetsSecrets.Veterinaria_Backend.models.Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        
        return usuario.getId();
    }
}