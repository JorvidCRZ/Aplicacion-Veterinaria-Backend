package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.*;
import com.PetsSecrets.Veterinaria_Backend.models.*;
import com.PetsSecrets.Veterinaria_Backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final ServicioRepository servicioRepository;
    private final SedeRepository sedeRepository;

    // Crear nueva cita
    public CitaResponse crearCita(CitaRequest request, Integer usuarioId) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que la mascota existe y pertenece al usuario
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        
        if (!mascota.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("La mascota no pertenece al usuario");
        }

        // Validar que el servicio existe
        Servicio servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Validar que la sede existe
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        // Validar disponibilidad de horario
        if (citaRepository.findCitaEnHorario(request.getFecha(), request.getHora(), request.getSedeId()).isPresent()) {
            throw new RuntimeException("El horario ya está ocupado");
        }

        // Validar que no sea en el pasado
        if (request.getFecha().isBefore(LocalDate.now()) || 
           (request.getFecha().isEqual(LocalDate.now()) && request.getHora().isBefore(LocalTime.now()))) {
            throw new RuntimeException("No se pueden agendar citas en el pasado");
        }

        // Crear la cita
        Cita cita = Cita.builder()
                .usuario(usuario)
                .mascota(mascota)
                .servicio(servicio)
                .sede(sede)
                .fecha(request.getFecha())
                .hora(request.getHora())
                .notas(request.getNotas())
                .estado(Cita.EstadoCita.pendiente)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Cita citaGuardada = citaRepository.save(cita);
        return CitaResponse.from(citaGuardada);
    }

    // Obtener todas las citas
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerTodasLasCitas() {
        return citaRepository.findAllOrderByFechaAndHora()
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener citas de un usuario
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorUsuario(Integer usuarioId) {
        return citaRepository.findByUsuarioIdOrderByFechaDescHoraDesc(usuarioId)
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener citas próximas de un usuario
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasProximasUsuario(Integer usuarioId) {
        return citaRepository.findCitasProximasUsuario(usuarioId, LocalDate.now())
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener citas por estado
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorEstado(Cita.EstadoCita estado) {
        return citaRepository.findByEstadoOrderByFechaAscHoraAsc(estado)
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener citas por fecha
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaOrderByHoraAsc(fecha)
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener citas por sede
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerCitasPorSede(Integer sedeId) {
        return citaRepository.findBySedeIdOrderByFechaAscHoraAsc(sedeId)
                .stream()
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener cita por ID
    @Transactional(readOnly = true)
    public CitaResponse obtenerCitaPorId(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        return CitaResponse.from(cita);
    }

    // Actualizar estado de cita (solo admin)
    public CitaResponse actualizarEstadoCita(Integer id, CitaUpdateRequest request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(request.getEstado());
        
        if (request.getFecha() != null) {
            cita.setFecha(request.getFecha());
        }
        
        if (request.getHora() != null) {
            cita.setHora(request.getHora());
        }
        
        if (request.getNotas() != null) {
            cita.setNotas(request.getNotas());
        }

        Cita citaActualizada = citaRepository.save(cita);
        return CitaResponse.from(citaActualizada);
    }

    // Reprogramar cita
    public CitaResponse reprogramarCita(Integer id, LocalDate nuevaFecha, LocalTime nuevaHora, Integer usuarioId) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Verificar que la cita pertenece al usuario o que es admin
        if (!cita.getUsuario().getId().equals(usuarioId)) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (usuario.getRol() != Usuario.Rol.admin) {
                throw new RuntimeException("No tienes permisos para reprogramar esta cita");
            }
        }

        // Validar que la cita se puede reprogramar
        if (cita.getEstado() == Cita.EstadoCita.completada || cita.getEstado() == Cita.EstadoCita.cancelada) {
            throw new RuntimeException("No se puede reprogramar una cita completada o cancelada");
        }

        // Validar disponibilidad del nuevo horario
        List<Cita> conflictos = citaRepository.findConflictosHorario(nuevaFecha, nuevaHora, cita.getSede().getId(), id);
        if (!conflictos.isEmpty()) {
            throw new RuntimeException("El nuevo horario ya está ocupado");
        }

        // Validar que no sea en el pasado
        if (nuevaFecha.isBefore(LocalDate.now()) || 
           (nuevaFecha.isEqual(LocalDate.now()) && nuevaHora.isBefore(LocalTime.now()))) {
            throw new RuntimeException("No se pueden agendar citas en el pasado");
        }

        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado(Cita.EstadoCita.pendiente); // Resetear a pendiente

        Cita citaActualizada = citaRepository.save(cita);
        return CitaResponse.from(citaActualizada);
    }

    // Cancelar cita
    public void cancelarCita(Integer id, Integer usuarioId) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // Verificar que la cita pertenece al usuario o que es admin
        if (!cita.getUsuario().getId().equals(usuarioId)) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (usuario.getRol() != Usuario.Rol.admin) {
                throw new RuntimeException("No tienes permisos para cancelar esta cita");
            }
        }

        cita.setEstado(Cita.EstadoCita.cancelada);
        citaRepository.save(cita);
    }

    // Buscar citas con filtros
    @Transactional(readOnly = true)
    public List<CitaResponse> buscarCitasConFiltros(
            Cita.EstadoCita estado,
            Integer usuarioId,
            Integer mascotaId,
            Integer sedeId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {
        
        List<Cita> citas = citaRepository.findAllOrderByFechaAndHora();
        
        return citas.stream()
                .filter(cita -> estado == null || cita.getEstado().equals(estado))
                .filter(cita -> usuarioId == null || cita.getUsuario().getId().equals(usuarioId))
                .filter(cita -> mascotaId == null || cita.getMascota().getId().equals(mascotaId))
                .filter(cita -> sedeId == null || cita.getSede().getId().equals(sedeId))
                .filter(cita -> fechaInicio == null || !cita.getFecha().isBefore(fechaInicio))
                .filter(cita -> fechaFin == null || !cita.getFecha().isAfter(fechaFin))
                .map(CitaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener estadísticas de citas
    @Transactional(readOnly = true)
    public CitaEstadisticas obtenerEstadisticas() {
        Long total = citaRepository.count();
        Long pendientes = citaRepository.countByEstado(Cita.EstadoCita.pendiente);
        Long confirmadas = citaRepository.countByEstado(Cita.EstadoCita.confirmada);
        Long completadas = citaRepository.countByEstado(Cita.EstadoCita.completada);
        Long canceladas = citaRepository.countByEstado(Cita.EstadoCita.cancelada);
        
        return CitaEstadisticas.builder()
                .totalCitas(total)
                .citasPendientes(pendientes)
                .citasConfirmadas(confirmadas)
                .citasCompletadas(completadas)
                .citasCanceladas(canceladas)
                .build();
    }

    // Verificar disponibilidad de horario
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora, Integer sedeId) {
        return citaRepository.findCitaEnHorario(fecha, hora, sedeId).isEmpty();
    }

    // Clase interna para estadísticas
    @lombok.Data
    @lombok.Builder
    public static class CitaEstadisticas {
        private Long totalCitas;
        private Long citasPendientes;
        private Long citasConfirmadas;
        private Long citasCompletadas;
        private Long citasCanceladas;
    }
}