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
public class AdopcionRequest {
    
    private Integer mascotaId;
    private Adopcion.ExperienciaMascotas experienciaMascotas;
    private Adopcion.TipoVivienda tipoVivienda;
    private String otrasMascotas;
    private String horarioTrabajo;
    private String motivoAdopcion;
    private String contactoEmergencia;
    private String veterinarioReferencia;
    private Boolean aceptaCondiciones;
    private Boolean aceptaVisita;
}