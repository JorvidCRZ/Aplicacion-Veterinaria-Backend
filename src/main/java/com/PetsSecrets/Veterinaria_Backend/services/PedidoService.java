package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.PedidoDetalleResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.PedidoRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.PedidoResponse;
import com.PetsSecrets.Veterinaria_Backend.models.*;
import com.PetsSecrets.Veterinaria_Backend.repositories.PedidoRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.PedidoDetalleRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;

    public List<PedidoResponse> obtenerTodosLosPedidos() {
        return pedidoRepository.findAllWithDetalles().stream()
                .map(this::convertirAPedidoResponse)
                .collect(Collectors.toList());
    }

    public List<PedidoResponse> obtenerPedidosPorUsuario(Integer usuarioId) {
        return pedidoRepository.findByUsuarioIdWithDetalles(usuarioId).stream()
                .map(this::convertirAPedidoResponse)
                .collect(Collectors.toList());
    }

    public PedidoResponse obtenerPedidoPorId(Integer id) {
        Pedido pedido = pedidoRepository.findByIdWithDetalles(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return convertirAPedidoResponse(pedido);
    }

    public PedidoResponse obtenerPedidoPorCodigo(String codigo) {
        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con código: " + codigo));
        return convertirAPedidoResponse(pedido);
    }

    @Transactional
    public PedidoResponse crearPedidoDesdeCarrito(Integer usuarioId, PedidoRequest request) {
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener carrito con items
        Carrito carrito = carritoService.obtenerCarritoPorUsuarioInterno(usuarioId);
        
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Verificar stock disponible para todos los productos
        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + 
                                         ". Stock disponible: " + producto.getStock() + 
                                         ", cantidad solicitada: " + item.getCantidad());
            }
        }

        // Calcular totales
        BigDecimal subtotal = carrito.getItems().stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal envio = request.getEnvio() != null ? request.getEnvio() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(envio);

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .codigo(generarCodigoPedido())
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now())
                .estado(Pedido.EstadoPedido.pendiente)
                .subtotal(subtotal)
                .envio(envio)
                .total(total)
                .direccion(request.getDireccion())
                .ciudad(request.getCiudad())
                .codigoPostal(request.getCodigoPostal())
                .telefonoContacto(request.getTelefonoContacto())
                .metodoPago(Pedido.MetodoPago.valueOf(request.getMetodoPago()))
                .fechaEntrega(request.getFechaEntrega())
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Crear detalles del pedido y actualizar stock
        List<PedidoDetalle> detallesList = new ArrayList<>();
        for (CarritoItem item : carrito.getItems()) {
            PedidoDetalle detalle = PedidoDetalle.builder()
                    .pedido(pedidoGuardado)
                    .producto(item.getProducto())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getSubtotal())
                    .build();

            PedidoDetalle detalleGuardado = pedidoDetalleRepository.save(detalle);
            detallesList.add(detalleGuardado);

            // Actualizar stock del producto
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoService.obtenerProductoPorIdInterno(producto.getId()); // Para guardar cambios
        }
        
        // Asociar los detalles al pedido
        pedidoGuardado.setDetalles(detallesList);

        // Limpiar carrito
        carritoService.vaciarCarrito(usuarioId);

        return convertirAPedidoResponse(pedidoRepository.findByIdWithDetalles(pedidoGuardado.getId()).get());
    }

    public PedidoResponse actualizarEstadoPedido(Integer id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        try {
            Pedido.EstadoPedido estado = Pedido.EstadoPedido.valueOf(nuevoEstado);
            pedido.setEstado(estado);
            Pedido pedidoActualizado = pedidoRepository.save(pedido);
            return convertirAPedidoResponse(pedidoActualizado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de pedido inválido: " + nuevoEstado);
        }
    }

    public List<PedidoResponse> obtenerPedidosPorEstado(String estado) {
        try {
            Pedido.EstadoPedido estadoEnum = Pedido.EstadoPedido.valueOf(estado);
            return pedidoRepository.findByEstado(estadoEnum).stream()
                    .map(this::convertirAPedidoResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de pedido inválido: " + estado);
        }
    }

    public PedidoResponse actualizarFechaEntrega(Integer id, String fechaEntrega) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        try {
            // Convertir la fecha string a LocalDate
            java.time.LocalDate fechaEntregaDate = java.time.LocalDate.parse(fechaEntrega);
            pedido.setFechaEntrega(fechaEntregaDate);
            Pedido pedidoActualizado = pedidoRepository.save(pedido);
            return convertirAPedidoResponse(pedidoActualizado);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar fecha de entrega: " + e.getMessage());
        }
    }

    private String generarCodigoPedido() {
        String codigo;
        do {
            codigo = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (pedidoRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private PedidoResponse convertirAPedidoResponse(Pedido pedido) {
        List<PedidoDetalleResponse> detalles = pedido.getDetalles() != null 
                ? pedido.getDetalles().stream()
                    .map(this::convertirAPedidoDetalleResponse)
                    .collect(Collectors.toList())
                : new ArrayList<>();

        return PedidoResponse.builder()
                .id(pedido.getId())
                .codigo(pedido.getCodigo())
                .usuarioId(pedido.getUsuario().getId())
                .usuarioNombre(pedido.getUsuario().getNombreCompleto())
                .usuarioEmail(pedido.getUsuario().getEmail())
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado().toString())
                .subtotal(pedido.getSubtotal())
                .envio(pedido.getEnvio())
                .total(pedido.getTotal())
                .direccion(pedido.getDireccion())
                .ciudad(pedido.getCiudad())
                .codigoPostal(pedido.getCodigoPostal())
                .telefonoContacto(pedido.getTelefonoContacto())
                .metodoPago(pedido.getMetodoPago().toString())
                .fechaEntrega(pedido.getFechaEntrega())
                .detalles(detalles)
                .build();
    }

    private PedidoDetalleResponse convertirAPedidoDetalleResponse(PedidoDetalle detalle) {
        return PedidoDetalleResponse.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoNombre(detalle.getProducto().getNombre())
                .productoImagenUrl(detalle.getProducto().getImagenUrl())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}