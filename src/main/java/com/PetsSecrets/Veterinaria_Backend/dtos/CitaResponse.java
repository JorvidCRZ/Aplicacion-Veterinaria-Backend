package com.PetsSecrets.Veterinaria_Backend.dtos;

import com.PetsSecrets.Veterinaria_Backend.models.Cita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaResponse {
    
    private Integer id;
    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private String usuarioTelefono;
    
    private Integer mascotaId;
    private String mascotaNombre;
    private String mascotaEspecie;
    private String mascotaRaza;
    private String mascotaFotoUrl;
    
    private Integer servicioId;
    private String servicioNombre;
    private String servicioDescripcion;
    private Double servicioPrecio;
    
    private Integer sedeId;
    private String sedeNombre;
    private String sedeDireccion;
    private String sedeTelefono;
    
    private LocalDate fecha;
    private LocalTime hora;
    private Cita.EstadoCita estado;
    private String notas;
    private LocalDateTime fechaCreacion;
    
    // Constructor estático para crear desde entidad
    public static CitaResponse from(Cita cita) {
        return CitaResponse.builder()
                .id(cita.getId())
                .usuarioId(cita.getUsuario().getId())
                .usuarioNombre(cita.getUsuario().getNombreCompleto())
                .usuarioEmail(cita.getUsuario().getEmail())
                .usuarioTelefono(cita.getUsuario().getTelefono())
                .mascotaId(cita.getMascota().getId())
                .mascotaNombre(cita.getMascota().getNombre())
                .mascotaEspecie(cita.getMascota().getEspecie().name())
                .mascotaRaza(cita.getMascota().getRaza())
                .mascotaFotoUrl(cita.getMascota().getFotoUrl())
                .servicioId(cita.getServicio().getId())
                .servicioNombre(cita.getServicio().getNombre())
                .servicioDescripcion(cita.getServicio().getDescripcion())
                .servicioPrecio(cita.getServicio().getPrecio())
                .sedeId(cita.getSede().getId())
                .sedeNombre(cita.getSede().getNombre())
                .sedeDireccion(cita.getSede().getDireccion())
                .sedeTelefono(cita.getSede().getTelefono())
                .fecha(cita.getFecha())
                .hora(cita.getHora())
                .estado(cita.getEstado())
                .notas(cita.getNotas())
                .fechaCreacion(cita.getFechaCreacion())
                .build();
    }
}