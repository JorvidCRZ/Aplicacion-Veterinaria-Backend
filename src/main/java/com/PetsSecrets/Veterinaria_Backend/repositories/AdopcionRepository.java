package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdopcionRepository extends JpaRepository<Adopcion, Integer> {
    
    // Buscar adopciones por usuario
    List<Adopcion> findByUsuarioIdOrderByFechaSolicitudDesc(Integer usuarioId);
    
    // Buscar adopciones por mascota
    List<Adopcion> findByMascotaIdOrderByFechaSolicitudDesc(Integer mascotaId);
    
    // Buscar adopciones por estado
    List<Adopcion> findByEstadoOrderByFechaSolicitudDesc(Adopcion.EstadoAdopcion estado);
    
    // Buscar adopción activa de una mascota
    @Query("SELECT a FROM Adopcion a WHERE a.mascota.id = :mascotaId AND a.estado IN ('pendiente', 'aprobada')")
    Optional<Adopcion> findAdopcionActivaByMascotaId(@Param("mascotaId") Integer mascotaId);
    
    // Verificar si usuario ya solicitó adopción para una mascota
    @Query("SELECT a FROM Adopcion a WHERE a.usuario.id = :usuarioId AND a.mascota.id = :mascotaId AND a.estado = 'pendiente'")
    Optional<Adopcion> findSolicitudPendienteByUsuarioAndMascota(@Param("usuarioId") Integer usuarioId, @Param("mascotaId") Integer mascotaId);
    
    // Buscar todas las adopciones ordenadas por fecha
    @Query("SELECT a FROM Adopcion a ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findAllOrderByFechaSolicitudDesc();
    
    // Contar adopciones por estado
    @Query("SELECT COUNT(a) FROM Adopcion a WHERE a.estado = :estado")
    Long countByEstado(@Param("estado") Adopcion.EstadoAdopcion estado);
    
    // Buscar adopciones por rango de fechas
    @Query("SELECT a FROM Adopcion a WHERE a.fechaSolicitud BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findByFechaSolicitudBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    // Buscar adopciones con filtros personalizados
    @Query("SELECT a FROM Adopcion a WHERE " +
           "(:estado IS NULL OR a.estado = :estado) " +
           "AND (:usuarioId IS NULL OR a.usuario.id = :usuarioId) " +
           "AND (:mascotaId IS NULL OR a.mascota.id = :mascotaId) " +
           "AND (:experiencia IS NULL OR a.experienciaMascotas = :experiencia) " +
           "AND (:tipoVivienda IS NULL OR a.tipoVivienda = :tipoVivienda) " +
           "ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findAdopcionesConFiltros(
        @Param("estado") Adopcion.EstadoAdopcion estado,
        @Param("usuarioId") Integer usuarioId,
        @Param("mascotaId") Integer mascotaId,
        @Param("experiencia") Adopcion.ExperienciaMascotas experiencia,
        @Param("tipoVivienda") Adopcion.TipoVivienda tipoVivienda
    );
    
    // Estadísticas de adopciones
    @Query("SELECT a.estado, COUNT(a) FROM Adopcion a GROUP BY a.estado")
    List<Object[]> getEstadisticasAdopciones();
    
    // Métodos para estadísticas específicas
    
    @Query("SELECT COUNT(a) FROM Adopcion a WHERE DATE(a.fechaAprobacion) BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaAdopcionBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
    
    // Contar adopciones por estado y rango de fechas de solicitud
    @Query("SELECT COUNT(a) FROM Adopcion a WHERE a.estado = :estado AND a.fechaSolicitud BETWEEN :fechaInicio AND :fechaFin")
    Long countByEstadoAndFechaSolicitudBetween(@Param("estado") Adopcion.EstadoAdopcion estado, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
