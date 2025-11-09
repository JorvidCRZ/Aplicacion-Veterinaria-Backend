package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    private double precio;
    private Integer stock;
    private String imagenUrl;
    private String estado;
    private LocalDateTime fechaCreacion;
    private CategoriaResponse categoria;
}