package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.MessageResponse;
import com.PetsSecrets.Veterinaria_Backend.dtos.ProductoRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.ProductoResponse;
import com.PetsSecrets.Veterinaria_Backend.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> obtenerTodosLosProductos() {
        try {
            List<ProductoResponse> productos = productoService.obtenerTodosLosProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosActivos() {
        try {
            List<ProductoResponse> productos = productoService.obtenerProductosActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerProductoPorId(@PathVariable Integer id) {
        try {
            ProductoResponse producto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosPorCategoria(
            @PathVariable Integer categoriaId) {
        try {
            List<ProductoResponse> productos = productoService.obtenerProductosPorCategoria(categoriaId);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El nombre del producto es requerido"));
            }
            if (request.getPrecio() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El precio debe ser mayor a 0"));
            }
            if (request.getCategoriaId() == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La categoría es requerida"));
            }

            ProductoResponse producto = productoService.crearProducto(request);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, 
                                               @RequestBody ProductoRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El nombre del producto es requerido"));
            }
            if (request.getPrecio() <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El precio debe ser mayor a 0"));
            }
            if (request.getCategoriaId() == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La categoría es requerida"));
            }

            ProductoResponse producto = productoService.actualizarProducto(id, request);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminarProducto(@PathVariable Integer id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(new MessageResponse("Producto desactivado exitosamente"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<MessageResponse> eliminarProductoPermanente(@PathVariable Integer id) {
        try {
            productoService.eliminarProductoPermanente(id);
            return ResponseEntity.ok(new MessageResponse("Producto eliminado permanentemente"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponse>> buscarProductosPorNombre(
            @RequestParam String nombre) {
        try {
            List<ProductoResponse> productos = productoService.buscarProductosPorNombre(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/precio")
    public ResponseEntity<List<ProductoResponse>> buscarProductosPorRangoPrecio(
            @RequestParam double min, @RequestParam double max) {
        try {
            List<ProductoResponse> productos = productoService.buscarProductosPorRangoPrecio(min, max);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosConStock() {
        try {
            List<ProductoResponse> productos = productoService.obtenerProductosConStock();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Integer id, 
                                            @RequestParam Integer stock) {
        try {
            if (stock < 0) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("El stock no puede ser negativo"));
            }

            ProductoResponse producto = productoService.actualizarStock(id, stock);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, 
                                          @RequestParam String estado) {
        try {
            ProductoResponse producto = productoService.cambiarEstadoProducto(id, estado);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }
}