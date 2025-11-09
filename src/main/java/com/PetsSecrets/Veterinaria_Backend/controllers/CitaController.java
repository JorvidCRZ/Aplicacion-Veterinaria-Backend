package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.*;
import com.PetsSecrets.Veterinaria_Backend.models.Cita;
import com.PetsSecrets.Veterinaria_Backend.services.CitaService;
import com.PetsSecrets.Veterinaria_Backend.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CitaController {

    private final CitaService citaService;
    private final UsuarioService usuarioService;

    // Crear nueva cita
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> crearCita(@Valid @RequestBody CitaRequest request,
                                      Authentication authentication) {
        try {
            Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
            CitaResponse cita = citaService.crearCita(request, usuarioId);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al crear cita: " + e.getMessage()));
        }
    }

    // Obtener todas las citas (solo admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerTodasLasCitas() {
        List<CitaResponse> citas = citaService.obtenerTodasLasCitas();
        return ResponseEntity.ok(citas);
    }

    // Obtener citas del usuario actual
    @GetMapping("/mis-citas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerMisCitas(Authentication authentication) {
        Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
        List<CitaResponse> citas = citaService.obtenerCitasPorUsuario(usuarioId);
        return ResponseEntity.ok(citas);
    }

    // Obtener citas próximas del usuario
    @GetMapping("/proximas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerCitasProximas(Authentication authentication) {
        Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
        List<CitaResponse> citas = citaService.obtenerCitasProximasUsuario(usuarioId);
        return ResponseEntity.ok(citas);
    }

    // Obtener citas por estado (solo admin)
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerPorEstado(@PathVariable Cita.EstadoCita estado) {
        List<CitaResponse> citas = citaService.obtenerCitasPorEstado(estado);
        return ResponseEntity.ok(citas);
    }

    // Obtener citas por fecha
    @GetMapping("/fecha/{fecha}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<CitaResponse> citas = citaService.obtenerCitasPorFecha(fecha);
        return ResponseEntity.ok(citas);
    }

    // Obtener citas por sede
    @GetMapping("/sede/{sedeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> obtenerPorSede(@PathVariable Integer sedeId) {
        List<CitaResponse> citas = citaService.obtenerCitasPorSede(sedeId);
        return ResponseEntity.ok(citas);
    }

    // Obtener cita por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> obtenerCitaPorId(@PathVariable Integer id,
                                             Authentication authentication) {
        try {
            CitaResponse cita = citaService.obtenerCitaPorId(id);
            
            // Verificar que la cita pertenece al usuario o que es admin
            Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
            if (!cita.getUsuarioId().equals(usuarioId) && 
                !authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403)
                        .body(new MessageResponse("No tienes permisos para ver esta cita"));
            }
            
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener cita: " + e.getMessage()));
        }
    }

    // Buscar citas con filtros
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CitaResponse>> buscarConFiltros(
            @RequestParam(required = false) Cita.EstadoCita estado,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer mascotaId,
            @RequestParam(required = false) Integer sedeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        List<CitaResponse> citas = citaService.buscarCitasConFiltros(estado, usuarioId, mascotaId, sedeId, fechaInicio, fechaFin);
        return ResponseEntity.ok(citas);
    }

    // Actualizar estado de cita (solo admin)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarEstado(@PathVariable Integer id,
                                             @Valid @RequestBody CitaUpdateRequest request) {
        try {
            CitaResponse cita = citaService.actualizarEstadoCita(id, request);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar estado: " + e.getMessage()));
        }
    }

    // Reprogramar cita
    @PutMapping("/{id}/reprogramar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> reprogramarCita(@PathVariable Integer id,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
                                           Authentication authentication) {
        try {
            Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
            CitaResponse cita = citaService.reprogramarCita(id, fecha, hora, usuarioId);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al reprogramar cita: " + e.getMessage()));
        }
    }

    // Cancelar cita
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelarCita(@PathVariable Integer id,
                                         Authentication authentication) {
        try {
            Integer usuarioId = usuarioService.buscarPorEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")).getId();
            citaService.cancelarCita(id, usuarioId);
            return ResponseEntity.ok(new MessageResponse("Cita cancelada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al cancelar cita: " + e.getMessage()));
        }
    }

    // Verificar disponibilidad de horario
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam Integer sedeId) {
        
        boolean disponible = citaService.verificarDisponibilidad(fecha, hora, sedeId);
        return ResponseEntity.ok(new DisponibilidadResponse(disponible, 
            disponible ? "Horario disponible" : "Horario no disponible"));
    }

    // Obtener estadísticas de citas (solo admin)
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CitaService.CitaEstadisticas> obtenerEstadisticas() {
        CitaService.CitaEstadisticas estadisticas = citaService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    // Clase interna para respuesta de disponibilidad
    public static class DisponibilidadResponse {
        public boolean disponible;
        public String mensaje;

        public DisponibilidadResponse(boolean disponible, String mensaje) {
            this.disponible = disponible;
            this.mensaje = mensaje;
        }
    }
}