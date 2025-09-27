package com.example.backend.repository;

import com.example.backend.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(path = "citas")
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    // Citas por usuario
    List<Cita> findByUsuario_Id(Integer usuarioId);

    // Citas por mascota
    List<Cita> findByMascota_Id(Integer mascotaId);

    // Citas por servicio
    List<Cita> findByServicio_Id(Integer servicioId);

    // Citas por sede
    List<Cita> findBySede_Id(Integer sedeId);

    // Citas en un rango de fechas
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // Citas por estado (ahora con enum)
    List<Cita> findByEstado(Cita.EstadoCita estado);

}