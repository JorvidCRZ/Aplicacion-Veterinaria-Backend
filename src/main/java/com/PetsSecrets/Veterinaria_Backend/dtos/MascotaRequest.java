package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaRequest {
    
    private String nombre;
    private Mascota.Especie especie;
    private String raza;
    private Mascota.Genero genero;
    private Integer edad;
    private Mascota.Tamano tamano;
    private String descripcion;
    private String fotoUrl;
    private Mascota.Tipo tipo;
    private Boolean vacunado;
    private Boolean esterilizado;
    private Boolean buenoConNinos;
    private Boolean buenoConOtrasMascotas;
    private String motivoAdopcion;
}