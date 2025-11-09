package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    Optional<Categoria> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    @Query("SELECT c FROM Categoria c WHERE c.nombre LIKE %:nombre%")
    java.util.List<Categoria> findByNombreContaining(String nombre);
}