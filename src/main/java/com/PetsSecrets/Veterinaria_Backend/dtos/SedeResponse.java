package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SedeResponse {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String ciudad;

    public static SedeResponse from(com.PetsSecrets.Veterinaria_Backend.models.Sede sede) {
        return SedeResponse.builder()
                .id(sede.getId())
                .nombre(sede.getNombre())
                .direccion(sede.getDireccion())
                .telefono(sede.getTelefono())
                .ciudad(sede.getCiudad())
                .build();
    }
}