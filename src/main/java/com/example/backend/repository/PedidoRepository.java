package com.example.backend.repository;

import com.example.backend.models.Pedido;
import com.example.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(path = "pedidos")
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    // Buscar pedidos por usuario
    List<Pedido> findByUsuario(Usuario usuario);

    // Buscar pedidos por estado
    List<Pedido> findByEstado(Pedido.EstadoPedido estado);

    // Buscar pedidos por ciudad
    List<Pedido> findByCiudad(String ciudad);

    // Buscar pedidos por método de pago
    List<Pedido> findByMetodoPago(Pedido.MetodoPago metodoPago);

    // Buscar pedidos recientes (ordenados por fecha)
    List<Pedido> findTop10ByOrderByFechaPedidoDesc();
}
