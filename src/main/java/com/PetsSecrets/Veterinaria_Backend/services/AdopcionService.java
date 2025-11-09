package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.AdopcionUpdateRequest;
import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.repositories.AdopcionRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.MascotaRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdopcionService {

    private final AdopcionRepository adopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaService mascotaService;

    // Crear nueva solicitud de adopción
    public AdopcionResponse crearSolicitudAdopcion(AdopcionRequest request, Integer usuarioId) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la mascota existe y está disponible
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(request.getMascotaId())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (mascota.getEstadoAdopcion() != Mascota.EstadoAdopcion.disponible) {
            throw new RuntimeException("La mascota no está disponible para adopción");
        }

        // Verificar que el usuario no haya solicitado ya esta mascota
        adopcionRepository.findSolicitudPendienteByUsuarioAndMascota(usuarioId, request.getMascotaId())
                .ifPresent(adopcion -> {
                    throw new RuntimeException("Ya tienes una solicitud pendiente para esta mascota");
                });

        // Crear nueva solicitud de adopción
        Adopcion adopcion = Adopcion.builder()
                .usuario(usuario)
                .mascota(mascota)
                .fechaSolicitud(LocalDateTime.now())
                .estado(Adopcion.EstadoAdopcion.pendiente)
                .experienciaMascotas(request.getExperienciaMascotas())
                .tipoVivienda(request.getTipoVivienda())
                .otrasMascotas(request.getOtrasMascotas())
                .horarioTrabajo(request.getHorarioTrabajo())
                .motivoAdopcion(request.getMotivoAdopcion())
                .contactoEmergencia(request.getContactoEmergencia())
                .veterinarioReferencia(request.getVeterinarioReferencia())
                .aceptaCondiciones(request.getAceptaCondiciones())
                .aceptaVisita(request.getAceptaVisita())
                .build();

        // Cambiar estado de la mascota a "en_proceso"
        mascotaService.cambiarEstadoAdopcion(request.getMascotaId(), Mascota.EstadoAdopcion.en_proceso);

        Adopcion adopcionGuardada = adopcionRepository.save(adopcion);
        return AdopcionResponse.from(adopcionGuardada);
    }

    // Obtener todas las adopciones
    @Transactional(readOnly = true)
    public List<AdopcionResponse> obtenerTodasLasAdopciones() {
        return adopcionRepository.findAllOrderByFechaSolicitudDesc()
                .stream()
                .map(AdopcionResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener adopciones de un usuario
    @Transactional(readOnly = true)
    public List<AdopcionResponse> obtenerAdopcionesPorUsuario(Integer usuarioId) {
        return adopcionRepository.findByUsuarioIdOrderByFechaSolicitudDesc(usuarioId)
                .stream()
                .map(AdopcionResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener adopciones por estado
    @Transactional(readOnly = true)
    public List<AdopcionResponse> obtenerAdopcionesPorEstado(Adopcion.EstadoAdopcion estado) {
        return adopcionRepository.findByEstadoOrderByFechaSolicitudDesc(estado)
                .stream()
                .map(AdopcionResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener adopción por ID
    @Transactional(readOnly = true)
    public AdopcionResponse obtenerAdopcionPorId(Integer id) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud de adopción no encontrada"));
        return AdopcionResponse.from(adopcion);
    }

    // Actualizar estado de adopción (solo para admin)
    public AdopcionResponse actualizarEstadoAdopcion(Integer id, AdopcionUpdateRequest request) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud de adopción no encontrada"));

        adopcion.setEstado(request.getEstado());

        // Si se aprueba, establecer fecha de aprobación
        if (request.getEstado() == Adopcion.EstadoAdopcion.aprobada) {
            adopcion.setFechaAprobacion(LocalDateTime.now());
        }

        // Manejar cambios en el estado de la mascota según el estado de adopción
        switch (request.getEstado()) {
            case pendiente:
                // Mantener mascota en proceso
                break;
            case aprobada:
                // Transferir propiedad de la mascota al usuario adoptante
                // Cambiar tipo de 'adopcion' a 'propia' y asignar nuevo dueño
                // Al aprobar, marcar como adoptado directamente
                mascotaService.transferirPropiedad(
                    adopcion.getMascota().getId(), 
                    adopcion.getUsuario().getId(), 
                    Mascota.EstadoAdopcion.adoptado
                );
                break;
            case completada:
                // Marcar mascota como adoptada (proceso completado)
                mascotaService.cambiarEstadoAdopcion(adopcion.getMascota().getId(), Mascota.EstadoAdopcion.adoptado);
                break;
            case rechazada:
                // Volver a poner la mascota como disponible si no hay otras solicitudes pendientes
                boolean hayOtrasSolicitudes = adopcionRepository
                    .findAdopcionActivaByMascotaId(adopcion.getMascota().getId())
                    .isPresent();
                if (!hayOtrasSolicitudes) {
                    mascotaService.cambiarEstadoAdopcion(adopcion.getMascota().getId(), Mascota.EstadoAdopcion.disponible);
                }
                break;
        }

        Adopcion adopcionActualizada = adopcionRepository.save(adopcion);
        return AdopcionResponse.from(adopcionActualizada);
    }

    // Cancelar solicitud de adopción (por el usuario solicitante)
    public void cancelarSolicitud(Integer id, Integer usuarioId) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud de adopción no encontrada"));

        // Verificar que el usuario es el solicitante
        if (!adopcion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para cancelar esta solicitud");
        }

        // Solo se puede cancelar si está pendiente
        if (adopcion.getEstado() != Adopcion.EstadoAdopcion.pendiente) {
            throw new RuntimeException("Solo se pueden cancelar solicitudes pendientes");
        }

        // Cambiar estado a rechazada (cancelada por el usuario)
        adopcion.setEstado(Adopcion.EstadoAdopcion.rechazada);
        adopcionRepository.save(adopcion);

        // Verificar si hay otras solicitudes activas para la mascota
        boolean hayOtrasSolicitudes = adopcionRepository
            .findAdopcionActivaByMascotaId(adopcion.getMascota().getId())
            .isPresent();
        
        if (!hayOtrasSolicitudes) {
            mascotaService.cambiarEstadoAdopcion(adopcion.getMascota().getId(), Mascota.EstadoAdopcion.disponible);
        }
    }

    // Buscar adopciones con filtros
    @Transactional(readOnly = true)
    public List<AdopcionResponse> buscarAdopcionesConFiltros(
            Adopcion.EstadoAdopcion estado,
            Integer usuarioId,
            Integer mascotaId,
            Adopcion.ExperienciaMascotas experiencia,
            Adopcion.TipoVivienda tipoVivienda) {
        
        return adopcionRepository.findAdopcionesConFiltros(estado, usuarioId, mascotaId, experiencia, tipoVivienda)
                .stream()
                .map(AdopcionResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener estadísticas de adopciones
    @Transactional(readOnly = true)
    public AdopcionEstadisticas obtenerEstadisticas() {
        Long pendientes = adopcionRepository.countByEstado(Adopcion.EstadoAdopcion.pendiente);
        Long aprobadas = adopcionRepository.countByEstado(Adopcion.EstadoAdopcion.aprobada);
        Long completadas = adopcionRepository.countByEstado(Adopcion.EstadoAdopcion.completada);
        Long rechazadas = adopcionRepository.countByEstado(Adopcion.EstadoAdopcion.rechazada);

        return AdopcionEstadisticas.builder()
                .pendientes(pendientes)
                .aprobadas(aprobadas)
                .completadas(completadas)
                .rechazadas(rechazadas)
                .total(pendientes + aprobadas + completadas + rechazadas)
                .build();
    }

    // Verificar si un usuario puede adoptar una mascota específica
    @Transactional(readOnly = true)
    public boolean puedeAdoptar(Integer usuarioId, Integer mascotaId) {
        // Verificar que la mascota está disponible
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(mascotaId)
                .orElse(null);
        
        if (mascota == null || mascota.getEstadoAdopcion() != Mascota.EstadoAdopcion.disponible) {
            return false;
        }

        // Verificar que no tiene solicitud pendiente
        return adopcionRepository
            .findSolicitudPendienteByUsuarioAndMascota(usuarioId, mascotaId)
            .isEmpty();
    }

    // Clase interna para estadísticas
    @lombok.Data
    @lombok.Builder
    public static class AdopcionEstadisticas {
        private Long pendientes;
        private Long aprobadas;
        private Long completadas;
        private Long rechazadas;
        private Long total;
    }
}