package com.example.backend.repository;

import com.example.backend.models.Adopcion;
import com.example.backend.models.Adopcion.EstadoAdopcion    ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "adopciones")
public interface AdopcionRepository extends JpaRepository<Adopcion, Integer> {
    //  Consultar por estado
    List<Adopcion> findByEstado(Adopcion.EstadoAdopcion estado);

    //  Consultar por usuario
    List<Adopcion> findByUsuario_Id(Integer usuarioId);

    //  Consultar por mascota
    List<Adopcion> findByMascota_Id(Integer mascotaId);

    //  Consultar por fecha de solicitud (>= una fecha dada)
    List<Adopcion> findByFechaSolicitudAfter(LocalDateTime fecha);

    //  Consultar por aprobaciones entre fechas
    List<Adopcion> findByFechaAprobacionBetween(LocalDateTime inicio, LocalDateTime fin);
}