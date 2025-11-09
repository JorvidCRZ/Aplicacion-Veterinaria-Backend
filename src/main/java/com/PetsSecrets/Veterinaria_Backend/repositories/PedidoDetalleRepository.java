package com.PetsSecrets.Veterinaria_Backend.repositories;

import com.PetsSecrets.Veterinaria_Backend.models.Pedido;
import com.PetsSecrets.Veterinaria_Backend.models.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Integer> {
    
    List<PedidoDetalle> findByPedido(Pedido pedido);
    
    List<PedidoDetalle> findByPedidoId(Integer pedidoId);
    
    void deleteByPedidoId(Integer pedidoId);
}