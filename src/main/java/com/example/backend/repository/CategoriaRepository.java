package com.example.backend.repository;

import com.example.backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//@RepositoryRestResource(path = "categorias", collectionResourceRel = "categorias")
@RepositoryRestResource(path = "categorias")
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Buscar categoría por nombre
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}