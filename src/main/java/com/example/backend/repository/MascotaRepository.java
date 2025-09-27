package com.example.backend.repository;

import com.example.backend.models.Mascota;
import com.example.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//@RepositoryRestResource(path = "mascotas", collectionResourceRel = "mascotas")
@RepositoryRestResource(path = "mascotas")
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    // Buscar todas las mascotas activas
    List<Mascota> findByActivoTrue();

    // Buscar mascotas por usuario
    List<Mascota> findByUsuario(Usuario usuario);

    // Buscar mascotas por estado de adopción
    List<Mascota> findByEstadoAdopcion(Mascota.EstadoAdopcion estado);

    // Buscar mascotas por especie
    List<Mascota> findByEspecie(Mascota.Especie especie);
}