package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
    
    // Buscar todas las sedes ordenadas por nombre
    List<Sede> findByOrderByNombre();
    
    // Buscar sedes por nombre
    List<Sede> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar sedes por ciudad
    @Query("SELECT s FROM Sede s WHERE LOWER(s.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')) ORDER BY s.nombre")
    List<Sede> findByCiudad(@Param("ciudad") String ciudad);
    
    // Buscar sede por teléfono
    List<Sede> findByTelefono(String telefono);
}