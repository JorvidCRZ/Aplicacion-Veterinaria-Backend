package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.ProductoRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.ProductoResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Categoria;
import com.PetsSecrets.Veterinaria_Backend.models.Producto;
import com.PetsSecrets.Veterinaria_Backend.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public List<ProductoResponse> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> obtenerProductosActivos() {
        return productoRepository.findByEstadoActivo().stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerProductoPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirAProductoResponse(producto);
    }

    public List<ProductoResponse> obtenerProductosPorCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaIdAndEstadoActivo(categoriaId).stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse crearProducto(ProductoRequest request) {
        Categoria categoria = categoriaService.obtenerCategoriaPorIdInterno(request.getCategoriaId());

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .imagenUrl(request.getImagenUrl())
                .estado(Producto.EstadoProducto.valueOf(
                    request.getEstado() != null ? request.getEstado() : "activo"))
                .fechaCreacion(LocalDateTime.now())
                .categoria(categoria)
                .build();

        Producto productoGuardado = productoRepository.save(producto);
        return convertirAProductoResponse(productoGuardado);
    }

    public ProductoResponse actualizarProducto(Integer id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        Categoria categoria = categoriaService.obtenerCategoriaPorIdInterno(request.getCategoriaId());

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock() != null ? request.getStock() : producto.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        
        if (request.getEstado() != null) {
            producto.setEstado(Producto.EstadoProducto.valueOf(request.getEstado()));
        }
        
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);
        return convertirAProductoResponse(productoActualizado);
    }

    public void eliminarProducto(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Eliminar físicamente el producto de la base de datos
        productoRepository.delete(producto);
    }

    public void eliminarProductoPermanente(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        productoRepository.delete(producto);
    }

    public List<ProductoResponse> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre).stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> buscarProductosPorRangoPrecio(double precioMin, double precioMax) {
        return productoRepository.findByPrecioBetween(precioMin, precioMax).stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> obtenerProductosConStock() {
        return productoRepository.findByStockAvailable().stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse actualizarStock(Integer id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        producto.setStock(nuevoStock);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirAProductoResponse(productoActualizado);
    }

    public ProductoResponse cambiarEstadoProducto(Integer id, String nuevoEstado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        try {
            producto.setEstado(Producto.EstadoProducto.valueOf(nuevoEstado));
            Producto productoActualizado = productoRepository.save(producto);
            return convertirAProductoResponse(productoActualizado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: " + nuevoEstado + ". Estados permitidos: activo, inactivo");
        }
    }

    // Método auxiliar para uso interno
    public Producto obtenerProductoPorIdInterno(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    private ProductoResponse convertirAProductoResponse(Producto producto) {
        CategoriaResponse categoriaResponse = CategoriaResponse.builder()
                .id(producto.getCategoria().getId())
                .nombre(producto.getCategoria().getNombre())
                .descripcion(producto.getCategoria().getDescripcion())
                .build();

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .imagenUrl(producto.getImagenUrl())
                .estado(producto.getEstado().toString())
                .fechaCreacion(producto.getFechaCreacion())
                .categoria(categoriaResponse)
                .build();
    }
}