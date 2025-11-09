package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Pedido;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.PetsSecrets.Veterinaria_Backend.models.Pedido.EstadoPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    List<Pedido> findByUsuario(Usuario usuario);
    
    List<Pedido> findByUsuarioId(Integer usuarioId);
    
    Optional<Pedido> findByCodigo(String codigo);
    
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles pd LEFT JOIN FETCH pd.producto WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetalles(@Param("id") Integer id);
    
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles pd LEFT JOIN FETCH pd.producto WHERE p.usuario.id = :usuarioId ORDER BY p.fechaPedido DESC")
    List<Pedido> findByUsuarioIdWithDetalles(@Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado")
    List<Pedido> findByEstado(@Param("estado") Pedido.EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    List<Pedido> findByFechaPedidoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles pd LEFT JOIN FETCH pd.producto ORDER BY p.fechaPedido DESC")
    List<Pedido> findAllWithDetalles();
    
    // Métodos para estadísticas
    Long countByEstado(EstadoPedido estado);
    
    Long countByEstadoAndFechaPedidoBetween(EstadoPedido estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Alias para compatibilidad con fechaCreacion
    default Long countByEstadoAndFechaCreacionBetween(EstadoPedido estado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return countByEstadoAndFechaPedidoBetween(estado, fechaInicio, fechaFin);
    }
    
    @Query("SELECT COALESCE(SUM(pd.cantidad), 0) FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = :estado")
    Long countProductosVendidos(@Param("estado") EstadoPedido estado);
    
    @Query("SELECT COALESCE(SUM(pd.cantidad), 0) FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = :estado AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Long countProductosVendidosEnRango(@Param("estado") EstadoPedido estado, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado = :estado")
    Double sumIngresosByEstado(@Param("estado") EstadoPedido estado);
    
    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado = :estado AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Double sumIngresosByEstadoAndFechaCreacionBetween(@Param("estado") EstadoPedido estado, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COALESCE(SUM(pd.cantidad), 0) FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = 'completado' AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Long countProductosVendidosPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p WHERE p.estado = 'completado' AND p.fechaPedido BETWEEN :fechaInicio AND :fechaFin")
    Double sumIngresosPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
