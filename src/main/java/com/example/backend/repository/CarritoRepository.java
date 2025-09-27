package com.example.backend.repository;

import com.example.backend.models.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "carritos")
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    // Buscar carrito por usuario
    Optional<Carrito> findByUsuario_Id(Integer usuarioId);

    //  Verificar si un usuario ya tiene carrito
    boolean existsByUsuario_Id(Integer usuarioId);
}
