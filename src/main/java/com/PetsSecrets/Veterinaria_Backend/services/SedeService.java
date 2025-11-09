package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.SedeResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Sede;
import com.PetsSecrets.Veterinaria_Backend.repositories.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;

    // Obtener todas las sedes como DTOs públicas
    public List<SedeResponse> obtenerTodasLasSedes() {
        return sedeRepository.findByOrderByNombre().stream()
                .map(SedeResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener todas las sedes (para uso interno)
    public List<Sede> obtenerTodasLasSedesInternal() {
        return sedeRepository.findByOrderByNombre();
    }

    // Obtener sede por ID como DTO público
    public SedeResponse obtenerSedePorId(Integer id) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        return SedeResponse.from(sede);
    }

    // Obtener sede por ID (para uso interno)
    public Sede obtenerSedePorIdInternal(Integer id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }

    // Buscar sedes por nombre como DTOs públicos
    public List<SedeResponse> buscarPorNombre(String nombre) {
        return sedeRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(SedeResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar sedes por ciudad como DTOs públicos
    public List<SedeResponse> buscarPorCiudad(String ciudad) {
        return sedeRepository.findByCiudad(ciudad).stream()
                .map(SedeResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar sede por teléfono como DTOs públicos
    public List<SedeResponse> buscarPorTelefono(String telefono) {
        return sedeRepository.findByTelefono(telefono).stream()
                .map(SedeResponse::from)
                .collect(Collectors.toList());
    }
}