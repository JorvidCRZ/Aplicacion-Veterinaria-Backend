package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    // Buscar citas por usuario
    List<Cita> findByUsuarioIdOrderByFechaDescHoraDesc(Integer usuarioId);
    
    // Buscar citas por mascota
    List<Cita> findByMascotaIdOrderByFechaDescHoraDesc(Integer mascotaId);
    
    // Buscar citas por estado
    List<Cita> findByEstadoOrderByFechaAscHoraAsc(Cita.EstadoCita estado);
    
    // Buscar citas por sede
    List<Cita> findBySedeIdOrderByFechaAscHoraAsc(Integer sedeId);
    
    // Buscar citas por servicio
    List<Cita> findByServicioIdOrderByFechaAscHoraAsc(Integer servicioId);
    
    // Métodos para estadísticas
    Long countByFechaAndEstado(LocalDate fecha, String estado);
    
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha >= :fechaInicio")
    Long countCitasUltimaSemana(@Param("fechaInicio") LocalDate fechaInicio);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha >= :fechaInicio")
    Long countCitasUltimoMes(@Param("fechaInicio") LocalDate fechaInicio);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha = :fecha")
    Long countByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long countCitasEntreFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);    Long countByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar citas por fecha
    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);
    
    // Buscar citas por fecha y sede
    List<Cita> findByFechaAndSedeIdOrderByHoraAsc(LocalDate fecha, Integer sedeId);
    
    // Verificar disponibilidad de horario
    @Query("SELECT c FROM Cita c WHERE c.fecha = :fecha AND c.hora = :hora AND c.sede.id = :sedeId AND c.estado != 'cancelada'")
    Optional<Cita> findCitaEnHorario(@Param("fecha") LocalDate fecha, 
                                    @Param("hora") LocalTime hora, 
                                    @Param("sedeId") Integer sedeId);
    
    // Buscar citas de un usuario para una fecha específica
    @Query("SELECT c FROM Cita c WHERE c.usuario.id = :usuarioId AND c.fecha = :fecha AND c.estado != 'cancelada'")
    List<Cita> findCitasUsuarioEnFecha(@Param("usuarioId") Integer usuarioId, @Param("fecha") LocalDate fecha);
    
    // Buscar todas las citas ordenadas por fecha y hora
    @Query("SELECT c FROM Cita c ORDER BY c.fecha ASC, c.hora ASC")
    List<Cita> findAllOrderByFechaAndHora();
    
    // Contar citas por estado
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Cita.EstadoCita estado);
    
    // Buscar citas por rango de fechas
    @Query("SELECT c FROM Cita c WHERE c.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fecha ASC, c.hora ASC")
    List<Cita> findCitasEnRangoFechas(@Param("fechaInicio") LocalDate fechaInicio, 
                                     @Param("fechaFin") LocalDate fechaFin);
    
    // Buscar citas próximas de un usuario
    @Query("SELECT c FROM Cita c WHERE c.usuario.id = :usuarioId AND c.fecha >= :fechaActual AND c.estado IN ('pendiente', 'confirmada') ORDER BY c.fecha ASC, c.hora ASC")
    List<Cita> findCitasProximasUsuario(@Param("usuarioId") Integer usuarioId, @Param("fechaActual") LocalDate fechaActual);
    
    // Buscar conflictos de horario para reprogramación
    @Query("SELECT c FROM Cita c WHERE c.fecha = :fecha AND c.hora = :hora AND c.sede.id = :sedeId AND c.id != :citaId AND c.estado != 'cancelada'")
    List<Cita> findConflictosHorario(@Param("fecha") LocalDate fecha, 
                                   @Param("hora") LocalTime hora, 
                                   @Param("sedeId") Integer sedeId,
                                   @Param("citaId") Integer citaId);
}