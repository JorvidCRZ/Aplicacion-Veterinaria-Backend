package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdopcionUpdateRequest {
    
    private Adopcion.EstadoAdopcion estado;
    private String motivoRechazo;
    private String observaciones;
}