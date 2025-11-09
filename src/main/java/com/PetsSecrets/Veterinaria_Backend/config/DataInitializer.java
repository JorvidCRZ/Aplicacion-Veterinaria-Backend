package com.PetsSecrets.Veterinaria_Backend.config;

import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.ProductoRequest;
import com.PetsSecrets.Veterinaria_Backend.services.CategoriaService;
import com.PetsSecrets.Veterinaria_Backend.services.ProductoService;
import com.PetsSecrets.Veterinaria_Backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private ProductoService productoService;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador por defecto
        usuarioService.crearUsuarioAdminSiNoExiste();
        
        // Inicializar datos de prueba
        inicializarDatosDePrueba();
    }
    
    private void inicializarDatosDePrueba() {
        try {
            // Verificar si ya existen categorías
            if (categoriaService.obtenerTodasLasCategorias().isEmpty()) {
                
                // Crear categorías
                CategoriaRequest alimento = CategoriaRequest.builder()
                        .nombre("Alimentos")
                        .descripcion("Alimentos para mascotas")
                        .build();
                var categoriaAlimento = categoriaService.crearCategoria(alimento);
                
                CategoriaRequest juguete = CategoriaRequest.builder()
                        .nombre("Juguetes")
                        .descripcion("Juguetes para mascotas")
                        .build();
                var categoriaJuguete = categoriaService.crearCategoria(juguete);
                
                CategoriaRequest accesorio = CategoriaRequest.builder()
                        .nombre("Accesorios")
                        .descripcion("Accesorios para mascotas")
                        .build();
                var categoriaAccesorio = categoriaService.crearCategoria(accesorio);
                
                // Crear productos de ejemplo
                ProductoRequest producto1 = ProductoRequest.builder()
                        .nombre("Alimento Premium para Perros")
                        .descripcion("Alimento balanceado premium para perros adultos")
                        .precio(45.90)
                        .stock(100)
                        .imagenUrl("assets/productos/producto1.webp")
                        .estado("activo")
                        .categoriaId(categoriaAlimento.getId())
                        .build();
                productoService.crearProducto(producto1);
                
                ProductoRequest producto2 = ProductoRequest.builder()
                        .nombre("Pelota de Goma")
                        .descripcion("Pelota resistente para juegos de perros")
                        .precio(15.50)
                        .stock(50)
                        .imagenUrl("assets/productos/producto4.webp")
                        .estado("activo")
                        .categoriaId(categoriaJuguete.getId())
                        .build();
                productoService.crearProducto(producto2);
                
                ProductoRequest producto3 = ProductoRequest.builder()
                        .nombre("Collar Ajustable")
                        .descripcion("Collar ajustable con hebilla de seguridad")
                        .precio(25.00)
                        .stock(30)
                        .imagenUrl("assets/productos/producto2.webp")
                        .estado("activo")
                        .categoriaId(categoriaAccesorio.getId())
                        .build();
                productoService.crearProducto(producto3);
                
                System.out.println("Datos de prueba inicializados correctamente");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar datos de prueba: " + e.getMessage());
        }
    }
}