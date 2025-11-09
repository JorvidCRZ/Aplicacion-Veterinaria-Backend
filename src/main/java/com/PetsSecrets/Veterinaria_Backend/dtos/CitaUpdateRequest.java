package com.PetsSecrets.Veterinaria_Backend.dtos;

import com.PetsSecrets.Veterinaria_Backend.models.Cita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaUpdateRequest {
    
    @NotNull(message = "El estado es obligatorio")
    private Cita.EstadoCita estado;
    
    private LocalDate fecha;
    
    private LocalTime hora;
    
    private String notas;
    
    private String comentarioAdmin;
}