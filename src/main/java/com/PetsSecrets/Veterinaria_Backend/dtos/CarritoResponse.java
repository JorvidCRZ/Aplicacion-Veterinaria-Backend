package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponse {
    private Integer id;
    private Integer usuarioId;
    private LocalDateTime fechaCreacion;
    private List<CarritoItemResponse> items;
    private BigDecimal total;
    private Integer totalItems;
}