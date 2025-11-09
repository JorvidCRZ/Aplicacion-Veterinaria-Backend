package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private Integer id;
    private String codigo;
    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal envio;
    private BigDecimal total;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String telefonoContacto;
    private String metodoPago;
    private LocalDate fechaEntrega;
    private List<PedidoDetalleResponse> detalles;
}