package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionUpdateRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import com.PetsSecrets.Veterinaria_Backend.services.AdopcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/adopciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdopcionController {

    private final AdopcionService adopcionService;
    private final UsuarioRepository usuarioRepository;

    // Crear nueva solicitud de adopción (requiere autenticación)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearSolicitudAdopcion(@Valid @RequestBody AdopcionRequest request,
                                                   Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            AdopcionResponse adopcion = adopcionService.crearSolicitudAdopcion(request, usuarioId);
            return ResponseEntity.ok(adopcion);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear solicitud: " + e.getMessage()));
        }
    }

    // Obtener todas las adopciones (solo admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdopcionResponse>> obtenerTodasLasAdopciones() {
        List<AdopcionResponse> adopciones = adopcionService.obtenerTodasLasAdopciones();
        return ResponseEntity.ok(adopciones);
    }

    // Obtener mis solicitudes de adopción (requiere autenticación)
    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<AdopcionResponse>> obtenerMisSolicitudes(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer usuarioId = getUserIdFromUserDetails(userDetails);
        
        List<AdopcionResponse> adopciones = adopcionService.obtenerAdopcionesPorUsuario(usuarioId);
        return ResponseEntity.ok(adopciones);
    }

    // Obtener adopción por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> obtenerAdopcionPorId(@PathVariable Integer id,
                                                 Authentication authentication) {
        try {
            AdopcionResponse adopcion = adopcionService.obtenerAdopcionPorId(id);
            
            // Verificar permisos: admin puede ver todas, usuario solo las suyas
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            if (!isAdmin(userDetails) && !adopcion.getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("No tienes permisos para ver esta solicitud"));
            }
            
            return ResponseEntity.ok(adopcion);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener solicitud: " + e.getMessage()));
        }
    }

    // Obtener adopciones por estado (solo admin)
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdopcionResponse>> obtenerPorEstado(@PathVariable Adopcion.EstadoAdopcion estado) {
        List<AdopcionResponse> adopciones = adopcionService.obtenerAdopcionesPorEstado(estado);
        return ResponseEntity.ok(adopciones);
    }

    // Buscar adopciones con filtros (solo admin)
    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdopcionResponse>> buscarConFiltros(
            @RequestParam(required = false) Adopcion.EstadoAdopcion estado,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer mascotaId,
            @RequestParam(required = false) Adopcion.ExperienciaMascotas experiencia,
            @RequestParam(required = false) Adopcion.TipoVivienda tipoVivienda) {
        
        List<AdopcionResponse> adopciones = adopcionService.buscarAdopcionesConFiltros(
                estado, usuarioId, mascotaId, experiencia, tipoVivienda);
        return ResponseEntity.ok(adopciones);
    }

    // Actualizar estado de adopción (solo admin)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstado(@PathVariable Integer id,
                                             @Valid @RequestBody AdopcionUpdateRequest request) {
        try {
            AdopcionResponse adopcion = adopcionService.actualizarEstadoAdopcion(id, request);
            return ResponseEntity.ok(adopcion);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar estado: " + e.getMessage()));
        }
    }

    // Cancelar solicitud (por el usuario solicitante)
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelarSolicitud(@PathVariable Integer id,
                                              Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Integer usuarioId = getUserIdFromUserDetails(userDetails);
            
            adopcionService.cancelarSolicitud(id, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Solicitud cancelada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cancelar solicitud: " + e.getMessage()));
        }
    }

    // Verificar si puede adoptar una mascota
    @GetMapping("/puede-adoptar/{mascotaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> puedeAdoptar(@PathVariable Integer mascotaId,
                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer usuarioId = getUserIdFromUserDetails(userDetails);
        
        boolean puedeAdoptar = adopcionService.puedeAdoptar(usuarioId, mascotaId);
        return ResponseEntity.ok(new MessageResponse("Puede adoptar: " + puedeAdoptar));
    }

    // Obtener estadísticas (solo admin)
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdopcionService.AdopcionEstadisticas> obtenerEstadisticas() {
        AdopcionService.AdopcionEstadisticas estadisticas = adopcionService.obtenerEstadisticas();
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

    // Método auxiliar para verificar si es admin
    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}