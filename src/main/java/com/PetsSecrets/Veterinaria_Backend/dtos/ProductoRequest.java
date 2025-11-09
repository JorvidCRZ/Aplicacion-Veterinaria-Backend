package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private double precio;
    private Integer stock;
    private String imagenUrl;
    private String estado;
    private Integer categoriaId;
}