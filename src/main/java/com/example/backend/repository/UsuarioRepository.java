package com.example.backend.repository;

import com.example.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@RepositoryRestResource(path = "usuarios", collectionResourceRel = "usuarios")
@RepositoryRestResource(path = "usuarios")
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}