package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Producto;
import com.PetsSecrets.Veterinaria_Backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    List<Producto> findByCategoria(Categoria categoria);
    
    List<Producto> findByCategoriaId(Integer categoriaId);
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'activo'")
    List<Producto> findByEstadoActivo();
    
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.estado = 'activo'")
    List<Producto> findByCategoriaIdAndEstadoActivo(@Param("categoriaId") Integer categoriaId);
    
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax")
    List<Producto> findByPrecioBetween(@Param("precioMin") double precioMin, @Param("precioMax") double precioMax);
    
    @Query("SELECT p FROM Producto p WHERE p.stock > 0")
    List<Producto> findByStockAvailable();
    
    // Métodos para estadísticas
    Long countByStockGreaterThan(Integer stock);
}