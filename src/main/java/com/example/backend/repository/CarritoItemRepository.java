package com.example.backend.repository;

import com.example.backend.models.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource(path = "carrito_items", collectionResourceRel = "carrito_items")
@RepositoryRestResource(path = "carrito-items")
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {
    // Buscar todos los items de un carrito
    List<CarritoItem> findByCarrito_Id(Integer carritoId);

    // Buscar si un producto ya está en un carrito
    Optional<CarritoItem> findByCarrito_IdAndProducto_Id(Integer carritoId, Integer productoId);

    // Eliminar todos los items de un carrito
    void deleteByCarrito_Id(Integer carritoId);
}