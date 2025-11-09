package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    
    // Buscar servicios activos
    List<Servicio> findByActivoTrueOrderByNombre();
    
    // Buscar servicios por nombre (contiene texto)
    List<Servicio> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // Buscar servicios por rango de precio
    @Query("SELECT s FROM Servicio s WHERE s.activo = true AND s.precio BETWEEN :precioMin AND :precioMax ORDER BY s.precio ASC")
    List<Servicio> findByRangoPrecio(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax);
}