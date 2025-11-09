package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasResponse {
    
    // Estadísticas principales del dashboard
    private Long usuariosRegistrados;
    private Long mascotasAdopcion;
    private Long pedidosPendientes;
    private Long citasHoy;
    private Long productosStock;
    private Double valorInventario;
    
    // Estadísticas adicionales para reportes
    private Long totalAdopciones;
    private Long citasSemana;
    private Long citasMes;
    private Long pedidosCompletados;
    private Long productosVendidos;
    private Double ingresosTotales;
    
    // Estadísticas por período (para reportes con fechas)
    private String periodoInicio;
    private String periodoFin;
}