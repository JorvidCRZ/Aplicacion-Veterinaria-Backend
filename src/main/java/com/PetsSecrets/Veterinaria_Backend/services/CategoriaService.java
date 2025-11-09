package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.CategoriaResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Categoria;
import com.PetsSecrets.Veterinaria_Backend.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirACategoriaResponse)
                .collect(Collectors.toList());
    }

    public CategoriaResponse obtenerCategoriaPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return convertirACategoriaResponse(categoria);
    }

    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return convertirACategoriaResponse(categoriaGuardada);
    }

    public CategoriaResponse actualizarCategoria(Integer id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // Verificar si el nuevo nombre ya existe (solo si es diferente al actual)
        if (!categoria.getNombre().equals(request.getNombre()) && 
            categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return convertirACategoriaResponse(categoriaActualizada);
    }

    public void eliminarCategoria(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        
        // Verificar si la categoría tiene productos asociados
        if (categoria.getProductos() != null && !categoria.getProductos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        
        categoriaRepository.delete(categoria);
    }

    public List<CategoriaResponse> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.findByNombreContaining(nombre).stream()
                .map(this::convertirACategoriaResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para uso interno
    public Categoria obtenerCategoriaPorIdInterno(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    private CategoriaResponse convertirACategoriaResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}