package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServicioResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;

    public static ServicioResponse from(com.PetsSecrets.Veterinaria_Backend.models.Servicio servicio) {
        return ServicioResponse.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .descripcion(servicio.getDescripcion())
                .precio(servicio.getPrecio())
                .build();
    }
}