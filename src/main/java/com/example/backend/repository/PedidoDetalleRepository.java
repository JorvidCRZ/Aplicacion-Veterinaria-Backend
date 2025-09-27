package com.example.backend.repository;

import com.example.backend.models.Pedido;
import com.example.backend.models.PedidoDetalle;
import com.example.backend.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "pedido-detalles")
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Integer> {
    // Obtener todos los detalles de un pedido
    List<PedidoDetalle> findByPedido(Pedido pedido);

    // Obtener todos los detalles de un producto
    List<PedidoDetalle> findByProducto(Producto producto);
}