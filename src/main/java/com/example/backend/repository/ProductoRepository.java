package com.example.backend.repository;

import com.example.backend.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "productos")
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Buscar productos por categoría
    List<Producto> findByCategoriaId(Integer categoriaId);

    // Buscar productos activos o inactivos
    List<Producto> findByEstado(Producto.EstadoProducto estado);

    // Buscar productos por nombre que contenga cierto texto (búsqueda tipo LIKE)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por precio menor o igual a cierto valor
    List<Producto> findByPrecioLessThanEqual(double precio);

    // Buscar productos por precio mayor o igual a cierto valor
    List<Producto> findByPrecioGreaterThanEqual(double precio);
}