package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    
    // Buscar mascotas disponibles para adopción
    List<Mascota> findByEstadoAdopcionAndActivoTrue(Mascota.EstadoAdopcion estadoAdopcion);
    
    // Buscar mascotas por especie
    List<Mascota> findByEspecieAndActivoTrue(Mascota.Especie especie);
    
    // Buscar mascotas por tipo
    List<Mascota> findByTipoAndActivoTrue(Mascota.Tipo tipo);
    
    // Buscar mascotas por usuario
    List<Mascota> findByUsuarioIdAndActivoTrue(Integer usuarioId);
    
    // Buscar por nombre (ignoring case)
    List<Mascota> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // Buscar mascotas activas
    List<Mascota> findByActivoTrue();
    
    // Buscar mascota por ID y activa
    Optional<Mascota> findByIdAndActivoTrue(Integer id);
    
    // Query personalizada para buscar mascotas disponibles con filtros
    @Query("SELECT m FROM Mascota m WHERE m.activo = true " +
           "AND (:especie IS NULL OR m.especie = :especie) " +
           "AND (:tamano IS NULL OR m.tamano = :tamano) " +
           "AND (:genero IS NULL OR m.genero = :genero) " +
           "AND (:estadoAdopcion IS NULL OR m.estadoAdopcion = :estadoAdopcion) " +
           "AND (:tipo IS NULL OR m.tipo = :tipo) " +
           "AND (:vacunado IS NULL OR m.vacunado = :vacunado) " +
           "AND (:esterilizado IS NULL OR m.esterilizado = :esterilizado) " +
           "ORDER BY m.fechaIngreso DESC")
    List<Mascota> findMascotasConFiltros(
        @Param("especie") Mascota.Especie especie,
        @Param("tamano") Mascota.Tamano tamano,
        @Param("genero") Mascota.Genero genero,
        @Param("estadoAdopcion") Mascota.EstadoAdopcion estadoAdopcion,
        @Param("tipo") Mascota.Tipo tipo,
        @Param("vacunado") Boolean vacunado,
        @Param("esterilizado") Boolean esterilizado
    );
    
    // Contar mascotas por estado de adopción
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.activo = true AND m.estadoAdopcion = :estado")
    Long countByEstadoAdopcion(@Param("estado") Mascota.EstadoAdopcion estado);
    
    // Contar mascotas ingresadas en un rango de fechas
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.activo = true AND m.fechaIngreso BETWEEN :fechaInicio AND :fechaFin")
    Long countByFechaIngresoBetween(@Param("fechaInicio") java.time.LocalDate fechaInicio, @Param("fechaFin") java.time.LocalDate fechaFin);
    
    // Buscar mascotas aptas para niños
    @Query("SELECT m FROM Mascota m WHERE m.activo = true AND m.buenoConNinos = true AND m.estadoAdopcion = 'disponible'")
    List<Mascota> findMascotasBuenasConNinos();
    

}