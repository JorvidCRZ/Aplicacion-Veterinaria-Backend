package com.example.backend.repository;

import com.example.backend.models.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "veterinarios")
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {

    // Buscar veterinarios por nombre (opcional)
    List<Veterinario> findByNombreCompletoContainingIgnoreCase(String nombre);

    // Buscar veterinarios por especialidad (opcional)
    List<Veterinario> findByEspecialidadContainingIgnoreCase(String especialidad);
}