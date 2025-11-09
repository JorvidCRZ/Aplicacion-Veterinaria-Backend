package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdopcionResponse {
    
    private Integer id;
    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private Integer mascotaId;
    private String mascotaNombre;
    private String mascotaEspecie;
    private String mascotaRaza;
    private String mascotaFotoUrl;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaAprobacion;
    private Adopcion.EstadoAdopcion estado;
    private Adopcion.ExperienciaMascotas experienciaMascotas;
    private Adopcion.TipoVivienda tipoVivienda;
    private String otrasMascotas;
    private String horarioTrabajo;
    private String motivoAdopcion;
    private String contactoEmergencia;
    private String veterinarioReferencia;
    private Boolean aceptaCondiciones;
    private Boolean aceptaVisita;
    
    // Constructor estático para crear desde entidad
    public static AdopcionResponse from(Adopcion adopcion) {
        return AdopcionResponse.builder()
                .id(adopcion.getId())
                .usuarioId(adopcion.getUsuario().getId())
                .usuarioNombre(adopcion.getUsuario().getNombre())
                .usuarioEmail(adopcion.getUsuario().getEmail())
                .mascotaId(adopcion.getMascota().getId())
                .mascotaNombre(adopcion.getMascota().getNombre())
                .mascotaEspecie(adopcion.getMascota().getEspecie().name())
                .mascotaRaza(adopcion.getMascota().getRaza())
                .mascotaFotoUrl(adopcion.getMascota().getFotoUrl())
                .fechaSolicitud(adopcion.getFechaSolicitud())
                .fechaAprobacion(adopcion.getFechaAprobacion())
                .estado(adopcion.getEstado())
                .experienciaMascotas(adopcion.getExperienciaMascotas())
                .tipoVivienda(adopcion.getTipoVivienda())
                .otrasMascotas(adopcion.getOtrasMascotas())
                .horarioTrabajo(adopcion.getHorarioTrabajo())
                .motivoAdopcion(adopcion.getMotivoAdopcion())
                .contactoEmergencia(adopcion.getContactoEmergencia())
                .veterinarioReferencia(adopcion.getVeterinarioReferencia())
                .aceptaCondiciones(adopcion.getAceptaCondiciones())
                .aceptaVisita(adopcion.getAceptaVisita())
                .build();
    }
}