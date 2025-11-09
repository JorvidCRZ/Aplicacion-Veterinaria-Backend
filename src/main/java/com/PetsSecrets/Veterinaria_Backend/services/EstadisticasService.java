package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.EstadisticasResponse;
import com.PetsSecrets.Veterinaria_Backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.PetsSecrets.Veterinaria_Backend.models.Mascota;
import com.PetsSecrets.Veterinaria_Backend.models.Pedido;
import com.PetsSecrets.Veterinaria_Backend.models.Adopcion;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EstadisticasService {
    
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final AdopcionRepository adopcionRepository;
    private final CitaRepository citaRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    
    public EstadisticasResponse obtenerEstadisticasDashboard() {
        LocalDate hoy = LocalDate.now();
        
        return EstadisticasResponse.builder()
                .usuariosRegistrados(usuarioRepository.count())
                .mascotasAdopcion(mascotaRepository.countByEstadoAdopcion(Mascota.EstadoAdopcion.disponible))
                .pedidosPendientes(pedidoRepository.countByEstado(Pedido.EstadoPedido.pendiente))
                .citasHoy(citaRepository.countByFecha(hoy))
                .productosStock(productoRepository.countByStockGreaterThan(0))
                .valorInventario(calcularValorInventario())
                .totalAdopciones(adopcionRepository.countByEstado(Adopcion.EstadoAdopcion.aprobada))
                .citasSemana(citaRepository.countCitasUltimaSemana(hoy.minusWeeks(1)))
                .citasMes(citaRepository.countCitasUltimoMes(hoy.minusMonths(1)))
                .pedidosCompletados(pedidoRepository.countByEstado(Pedido.EstadoPedido.entregado))
                .productosVendidos(calcularProductosVendidos())
                .ingresosTotales(calcularIngresosTotales())
                .build();
    }
    
    public EstadisticasResponse obtenerReporte(String fechaInicio, String fechaFin) {
        LocalDate hoy = LocalDate.now();
        
        // Si no se proporcionan fechas, usar valores por defecto
        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : hoy.minusMonths(1);
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : hoy;
        
        // Convertir a LocalDateTime para las consultas
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        
        return EstadisticasResponse.builder()
                .usuariosRegistrados(usuarioRepository.countByFechaRegistroBetween(inicioDateTime, finDateTime))
                .mascotasAdopcion(mascotaRepository.countByFechaIngresoBetween(inicio, fin))
                .pedidosPendientes(pedidoRepository.countByEstadoAndFechaCreacionBetween(Pedido.EstadoPedido.pendiente, inicioDateTime, finDateTime))
                .citasHoy(citaRepository.countByFecha(hoy))
                .productosStock(productoRepository.countByStockGreaterThan(0))
                .valorInventario(calcularValorInventario())
                .totalAdopciones(adopcionRepository.countByEstadoAndFechaSolicitudBetween(Adopcion.EstadoAdopcion.aprobada, inicioDateTime, finDateTime))
                .citasSemana(citaRepository.countCitasEntreFechas(inicio, fin))
                .citasMes(citaRepository.countCitasEntreFechas(inicio, fin))
                .pedidosCompletados(pedidoRepository.countByEstadoAndFechaCreacionBetween(Pedido.EstadoPedido.entregado, inicioDateTime, finDateTime))
                .productosVendidos(calcularProductosVendidosEnRango(inicioDateTime, finDateTime))
                .ingresosTotales(calcularIngresosTotalesEnRango(inicioDateTime, finDateTime))
                .build();
    }

    private Double calcularValorInventario() {
        // Calcular el valor total del inventario (stock * precio)
        return productoRepository.findAll().stream()
                .mapToDouble(producto -> producto.getStock() * producto.getPrecio())
                .sum();
    }
    
    private Long calcularProductosVendidos() {
        // Contar productos vendidos basado en pedidos completados
        return pedidoRepository.countProductosVendidos(Pedido.EstadoPedido.entregado);
    }
    
    private Long calcularProductosVendidosEnRango(LocalDateTime inicio, LocalDateTime fin) {
        // Contar productos vendidos en el rango de fechas
        return pedidoRepository.countProductosVendidosEnRango(Pedido.EstadoPedido.entregado, inicio, fin);
    }
    
    private Double calcularIngresosTotales() {
        // Sumar el total de pedidos completados
        return pedidoRepository.sumIngresosByEstado(Pedido.EstadoPedido.entregado);
    }
    
    private Double calcularIngresosTotalesEnRango(LocalDateTime inicio, LocalDateTime fin) {
        // Sumar ingresos en el rango de fechas
        return pedidoRepository.sumIngresosByEstadoAndFechaCreacionBetween(Pedido.EstadoPedido.entregado, inicio, fin);
    }
}