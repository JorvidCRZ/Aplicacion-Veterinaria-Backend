package com.example.backend.repository;

import com.example.backend.models.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "sedes")
@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
    // Buscar sedes por ciudad
    List<Sede> findByCiudad(String ciudad);

    // Buscar sedes por nombre que contenga cierto texto
    List<Sede> findByNombreContainingIgnoreCase(String nombre);
}