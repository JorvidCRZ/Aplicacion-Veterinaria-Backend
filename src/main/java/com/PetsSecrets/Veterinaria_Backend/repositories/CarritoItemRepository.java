package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.CarritoItem;
import com.PetsSecrets.Veterinaria_Backend.models.Carrito;
import com.PetsSecrets.Veterinaria_Backend.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {
    
    List<CarritoItem> findByCarrito(Carrito carrito);
    
    List<CarritoItem> findByCarritoId(Integer carritoId);
    
    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
    
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carrito.id = :carritoId AND ci.producto.id = :productoId")
    Optional<CarritoItem> findByCarritoIdAndProductoId(@Param("carritoId") Integer carritoId, @Param("productoId") Integer productoId);
    
    @Modifying
    @Query("DELETE FROM CarritoItem ci WHERE ci.carrito.id = :carritoId AND ci.producto.id = :productoId")
    void deleteByCarritoIdAndProductoId(@Param("carritoId") Integer carritoId, @Param("productoId") Integer productoId);
    
    @Modifying
    void deleteByCarritoAndProducto(Carrito carrito, Producto producto);
    
    void deleteByCarritoId(Integer carritoId);
}