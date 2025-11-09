package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.MascotaRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.MascotaResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.repositories.MascotaRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    // Crear nueva mascota
    public MascotaResponse crearMascota(MascotaRequest request, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = Mascota.builder()
                .nombre(request.getNombre())
                .especie(request.getEspecie())
                .raza(request.getRaza())
                .genero(request.getGenero())
                .edad(request.getEdad())
                .tamano(request.getTamano())
                .descripcion(request.getDescripcion())
                .fotoUrl(request.getFotoUrl())
                .tipo(request.getTipo() != null ? request.getTipo() : Mascota.Tipo.propia)
                .estadoAdopcion(request.getTipo() == Mascota.Tipo.adopcion ? 
                    Mascota.EstadoAdopcion.disponible : Mascota.EstadoAdopcion.no_disponible)
                .vacunado(request.getVacunado() != null ? request.getVacunado() : false)
                .esterilizado(request.getEsterilizado() != null ? request.getEsterilizado() : false)
                .buenoConNinos(request.getBuenoConNinos() != null ? request.getBuenoConNinos() : false)
                .buenoConOtrasMascotas(request.getBuenoConOtrasMascotas() != null ? 
                    request.getBuenoConOtrasMascotas() : false)
                .fechaIngreso(LocalDate.now())
                .usuario(usuario)
                .activo(true)
                .build();

        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return MascotaResponse.from(mascotaGuardada);
    }

    // Obtener todas las mascotas activas
    @Transactional(readOnly = true)
    public List<MascotaResponse> obtenerTodasLasMascotas() {
        return mascotaRepository.findByActivoTrue()
                .stream()
                .map(MascotaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener mascotas disponibles para adopción
    @Transactional(readOnly = true)
    public List<MascotaResponse> obtenerMascotasDisponibles() {
        return mascotaRepository.findByEstadoAdopcionAndActivoTrue(Mascota.EstadoAdopcion.disponible)
                .stream()
                .map(MascotaResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener mascota por ID
    @Transactional(readOnly = true)
    public MascotaResponse obtenerMascotaPorId(Integer id) {
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        return MascotaResponse.from(mascota);
    }

    // Obtener mascotas de un usuario
    @Transactional(readOnly = true)
    public List<MascotaResponse> obtenerMascotasPorUsuario(Integer usuarioId) {
        return mascotaRepository.findByUsuarioIdAndActivoTrue(usuarioId)
                .stream()
                .filter(mascota -> mascota.getTipo() == Mascota.Tipo.propia) // Solo mascotas propias
                .map(MascotaResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar mascotas por filtros
    @Transactional(readOnly = true)
    public List<MascotaResponse> buscarMascotasConFiltros(
            Mascota.Especie especie,
            Mascota.Tamano tamano,
            Mascota.Genero genero,
            Mascota.EstadoAdopcion estadoAdopcion,
            Mascota.Tipo tipo,
            Boolean vacunado,
            Boolean esterilizado) {
        
        return mascotaRepository.findMascotasConFiltros(
                especie, tamano, genero, estadoAdopcion, tipo, vacunado, esterilizado)
                .stream()
                .map(MascotaResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar mascotas por nombre
    @Transactional(readOnly = true)
    public List<MascotaResponse> buscarPorNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(MascotaResponse::from)
                .collect(Collectors.toList());
    }

    // Actualizar mascota
    public MascotaResponse actualizarMascota(Integer id, MascotaRequest request, Integer usuarioId) {
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // Verificar que el usuario sea el propietario o admin
        if (!mascota.getUsuario().getId().equals(usuarioId)) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (!usuario.getRol().name().equals("ADMIN")) {
                throw new RuntimeException("No tienes permisos para modificar esta mascota");
            }
        }

        // Actualizar campos
        if (request.getNombre() != null) mascota.setNombre(request.getNombre());
        if (request.getEspecie() != null) mascota.setEspecie(request.getEspecie());
        if (request.getRaza() != null) mascota.setRaza(request.getRaza());
        if (request.getGenero() != null) mascota.setGenero(request.getGenero());
        if (request.getEdad() != null) mascota.setEdad(request.getEdad());
        if (request.getTamano() != null) mascota.setTamano(request.getTamano());
        if (request.getDescripcion() != null) mascota.setDescripcion(request.getDescripcion());
        if (request.getFotoUrl() != null) mascota.setFotoUrl(request.getFotoUrl());
        if (request.getVacunado() != null) mascota.setVacunado(request.getVacunado());
        if (request.getEsterilizado() != null) mascota.setEsterilizado(request.getEsterilizado());
        if (request.getBuenoConNinos() != null) mascota.setBuenoConNinos(request.getBuenoConNinos());
        if (request.getBuenoConOtrasMascotas() != null) mascota.setBuenoConOtrasMascotas(request.getBuenoConOtrasMascotas());

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return MascotaResponse.from(mascotaActualizada);
    }

    // Cambiar estado de adopción
    public MascotaResponse cambiarEstadoAdopcion(Integer id, Mascota.EstadoAdopcion nuevoEstado) {
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        mascota.setEstadoAdopcion(nuevoEstado);
        
        // Si se adopta, establecer fecha de adopción
        if (nuevoEstado == Mascota.EstadoAdopcion.adoptado) {
            mascota.setFechaAdopcion(LocalDate.now());
        }

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return MascotaResponse.from(mascotaActualizada);
    }

    // Transferir propiedad de mascota cuando se aprueba adopción
    public MascotaResponse transferirPropiedad(Integer mascotaId, Integer nuevoUsuarioId, Mascota.EstadoAdopcion nuevoEstado) {
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        Usuario nuevoUsuario = usuarioRepository.findById(nuevoUsuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cambiar el tipo de 'adopcion' a 'propia'
        mascota.setTipo(Mascota.Tipo.propia);
        
        // Asignar nuevo dueño
        mascota.setUsuario(nuevoUsuario);
        
        // Actualizar estado de adopción
        mascota.setEstadoAdopcion(nuevoEstado);
        
        // Si se completa la adopción, establecer fecha
        if (nuevoEstado == Mascota.EstadoAdopcion.adoptado) {
            mascota.setFechaAdopcion(LocalDate.now());
        }

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return MascotaResponse.from(mascotaActualizada);
    }

    // Eliminar mascota (soft delete)
    public void eliminarMascota(Integer id, Integer usuarioId) {
        Mascota mascota = mascotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // Verificar permisos
        if (!mascota.getUsuario().getId().equals(usuarioId)) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            if (!usuario.getRol().name().equals("ADMIN")) {
                throw new RuntimeException("No tienes permisos para eliminar esta mascota");
            }
        }

        mascota.setActivo(false);
        mascotaRepository.save(mascota);
    }

    // Obtener estadísticas de mascotas
    @Transactional(readOnly = true)
    public MascotaEstadisticas obtenerEstadisticas() {
        Long disponibles = mascotaRepository.countByEstadoAdopcion(Mascota.EstadoAdopcion.disponible);
        Long adoptadas = mascotaRepository.countByEstadoAdopcion(Mascota.EstadoAdopcion.adoptado);
        Long enProceso = mascotaRepository.countByEstadoAdopcion(Mascota.EstadoAdopcion.en_proceso);
        Long noDisponibles = mascotaRepository.countByEstadoAdopcion(Mascota.EstadoAdopcion.no_disponible);

        return MascotaEstadisticas.builder()
                .disponibles(disponibles)
                .adoptadas(adoptadas)
                .enProceso(enProceso)
                .noDisponibles(noDisponibles)
                .total(disponibles + adoptadas + enProceso + noDisponibles)
                .build();
    }

    // Clase interna para estadísticas
    @lombok.Data
    @lombok.Builder
    public static class MascotaEstadisticas {
        private Long disponibles;
        private Long adoptadas;
        private Long enProceso;
        private Long noDisponibles;
        private Long total;
    }
}