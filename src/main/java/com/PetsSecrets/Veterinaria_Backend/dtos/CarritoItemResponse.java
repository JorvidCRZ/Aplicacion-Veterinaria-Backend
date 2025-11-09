package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemResponse {
    private Integer id;
    private Integer productoId;
    private String productoNombre;
    private String productoImagenUrl;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}