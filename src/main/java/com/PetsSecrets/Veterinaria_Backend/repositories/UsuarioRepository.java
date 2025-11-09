package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<Usuario> findByEmailAndPasswordHash(String email, String passwordHash);
    
    // Métodos para estadísticas
    Long countByFechaRegistroBetween(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);
}