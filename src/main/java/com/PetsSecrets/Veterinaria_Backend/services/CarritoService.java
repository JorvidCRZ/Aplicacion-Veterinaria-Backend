package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.CarritoItemRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.CarritoItemResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.CarritoResponse;
import com.PetsSecrets.Veterinaria_Backend.models.*;
import com.PetsSecrets.Veterinaria_Backend.repositories.CarritoRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.CarritoItemRepository;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;

    public CarritoResponse obtenerCarritoPorUsuario(Integer usuarioId) {
        Optional<Carrito> carritoOpt = carritoRepository.findByUsuarioIdWithItems(usuarioId);
        
        if (carritoOpt.isEmpty()) {
            // Crear carrito vacío si no existe
            return crearCarritoVacio(usuarioId);
        }
        
        return convertirACarritoResponse(carritoOpt.get());
    }

    public CarritoResponse agregarProductoAlCarrito(Integer usuarioId, CarritoItemRequest request) {
        // Obtener o crear carrito
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        
        // Obtener producto
        Producto producto = productoService.obtenerProductoPorIdInterno(request.getProductoId());
        
        // Verificar stock disponible
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }

        // Buscar si el producto ya está en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByCarritoAndProducto(carrito, producto);

        if (itemExistente.isPresent()) {
            // Actualizar cantidad del item existente
            CarritoItem item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock() + 
                                         ", cantidad en carrito: " + item.getCantidad());
            }
            
            item.setCantidad(nuevaCantidad);
            carritoItemRepository.save(item);
        } else {
            // Crear nuevo item en el carrito
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(request.getCantidad())
                    .precioUnitario(BigDecimal.valueOf(producto.getPrecio()))
                    .build();
            
            carritoItemRepository.save(nuevoItem);
        }

        // Obtener carrito actualizado
        carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId).get();
        return convertirACarritoResponse(carrito);
    }

    public CarritoResponse actualizarCantidadItem(Integer usuarioId, Integer itemId, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return eliminarItemDelCarrito(usuarioId, itemId);
        }

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado con ID: " + itemId));

        // Verificar que el item pertenece al usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para modificar este item");
        }

        // Verificar stock disponible
        if (item.getProducto().getStock() < nuevaCantidad) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + item.getProducto().getStock());
        }

        item.setCantidad(nuevaCantidad);
        carritoItemRepository.save(item);

        // Obtener carrito actualizado
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId).get();
        return convertirACarritoResponse(carrito);
    }

    public CarritoResponse eliminarItemDelCarrito(Integer usuarioId, Integer itemId) {
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado con ID: " + itemId));

        // Verificar que el item pertenece al usuario
        if (!item.getCarrito().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para eliminar este item");
        }

        carritoItemRepository.delete(item);

        // Obtener carrito actualizado
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId).get();
        return convertirACarritoResponse(carrito);
    }

    public void vaciarCarrito(Integer usuarioId) {
        Optional<Carrito> carritoOpt = carritoRepository.findByUsuarioId(usuarioId);
        if (carritoOpt.isPresent()) {
            carritoItemRepository.deleteByCarritoId(carritoOpt.get().getId());
        }
    }

    @Transactional
    public CarritoResponse eliminarProductoDelCarrito(Integer usuarioId, Integer productoId) {
        Carrito carrito = obtenerCarritoPorUsuarioInterno(usuarioId);
        
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);
        
        if (itemOpt.isPresent()) {
            carritoItemRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
            carritoItemRepository.flush();
        }

        // Obtener carrito actualizado
        carrito = carritoRepository.findByUsuarioIdWithItems(usuarioId).orElseThrow();
        return convertirACarritoResponse(carrito);
    }

    // Método auxiliar para obtener carrito interno
    public Carrito obtenerCarritoPorUsuarioInterno(Integer usuarioId) {
        return carritoRepository.findByUsuarioIdWithItems(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario: " + usuarioId));
    }

    private Carrito obtenerOCrearCarrito(Integer usuarioId) {
        Optional<Carrito> carritoOpt = carritoRepository.findByUsuarioId(usuarioId);
        
        if (carritoOpt.isPresent()) {
            return carritoOpt.get();
        }

        // Crear nuevo carrito
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        Carrito nuevoCarrito = Carrito.builder()
                .usuario(usuario)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return carritoRepository.save(nuevoCarrito);
    }

    private CarritoResponse crearCarritoVacio(Integer usuarioId) {
        // Crear el objeto Usuario para la relación
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // Crear un nuevo carrito en la base de datos
        Carrito nuevoCarrito = Carrito.builder()
                .usuario(usuario)
                .build();
                
        Carrito carritoGuardado = carritoRepository.save(nuevoCarrito);
        return convertirACarritoResponse(carritoGuardado);
    }

    private CarritoResponse convertirACarritoResponse(Carrito carrito) {
        List<CarritoItemResponse> items = carrito.getItems() != null ? 
                carrito.getItems().stream()
                        .map(this::convertirACarritoItemResponse)
                        .collect(Collectors.toList()) 
                : new ArrayList<>();

        BigDecimal total = items.stream()
                .map(CarritoItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = items.stream()
                .mapToInt(CarritoItemResponse::getCantidad)
                .sum();

        return CarritoResponse.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuario().getId())
                .fechaCreacion(carrito.getFechaCreacion())
                .items(items)
                .total(total)
                .totalItems(totalItems)
                .build();
    }

    private CarritoItemResponse convertirACarritoItemResponse(CarritoItem item) {
        return CarritoItemResponse.builder()
                .id(item.getId())
                .productoId(item.getProducto().getId())
                .productoNombre(item.getProducto().getNombre())
                .productoImagenUrl(item.getProducto().getImagenUrl())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}