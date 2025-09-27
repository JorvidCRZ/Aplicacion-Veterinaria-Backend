package com.example.backend.repository;

import com.example.backend.models.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "servicios")
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    // Buscar servicios activos
    List<Servicio> findByActivoTrue();

    // Buscar servicios por veterinario
    List<Servicio> findByVeterinarioId(Integer veterinarioId);

    // Buscar servicios por nombre que contenga cierto texto (buscador)
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);
}