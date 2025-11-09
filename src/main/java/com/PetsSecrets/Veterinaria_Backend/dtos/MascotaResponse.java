package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaResponse {
    
    private Integer id;
    private String nombre;
    private Mascota.Especie especie;
    private String raza;
    private Mascota.Genero genero;
    private Integer edad;
    private Mascota.Tamano tamano;
    private String descripcion;
    private String fotoUrl;
    private Mascota.Tipo tipo;
    private Mascota.EstadoAdopcion estadoAdopcion;
    private Boolean vacunado;
    private Boolean esterilizado;
    private Boolean buenoConNinos;
    private Boolean buenoConOtrasMascotas;
    private LocalDate fechaIngreso;
    private LocalDate fechaAdopcion;
    private String usuarioNombre;
    private Integer usuarioId;
    
    // Constructor estático para crear desde entidad
    public static MascotaResponse from(Mascota mascota) {
        return MascotaResponse.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .especie(mascota.getEspecie())
                .raza(mascota.getRaza())
                .genero(mascota.getGenero())
                .edad(mascota.getEdad())
                .tamano(mascota.getTamano())
                .descripcion(mascota.getDescripcion())
                .fotoUrl(mascota.getFotoUrl())
                .tipo(mascota.getTipo())
                .estadoAdopcion(mascota.getEstadoAdopcion())
                .vacunado(mascota.getVacunado())
                .esterilizado(mascota.getEsterilizado())
                .buenoConNinos(mascota.getBuenoConNinos())
                .buenoConOtrasMascotas(mascota.getBuenoConOtrasMascotas())
                .fechaIngreso(mascota.getFechaIngreso())
                .fechaAdopcion(mascota.getFechaAdopcion())
                .usuarioNombre(mascota.getUsuario() != null ? mascota.getUsuario().getNombre() : null)
                .usuarioId(mascota.getUsuario() != null ? mascota.getUsuario().getId() : null)
                .build();
    }
}