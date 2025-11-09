package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequest {
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String telefonoContacto;
    private String metodoPago;
    private LocalDate fechaEntrega;
    private BigDecimal envio;
}