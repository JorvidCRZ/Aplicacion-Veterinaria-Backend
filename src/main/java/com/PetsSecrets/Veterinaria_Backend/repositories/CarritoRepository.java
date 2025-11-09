package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Carrito;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    
    Optional<Carrito> findByUsuario(Usuario usuario);
    
    Optional<Carrito> findByUsuarioId(Integer usuarioId);
    
    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.producto WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdWithItems(@Param("usuarioId") Integer usuarioId);
    
    boolean existsByUsuarioId(Integer usuarioId);
}