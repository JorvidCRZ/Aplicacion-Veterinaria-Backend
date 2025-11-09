package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.ServicioResponse;
import com.PetsSecrets.Veterinaria_Backend.models.Servicio;
import com.PetsSecrets.Veterinaria_Backend.repositories.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    // Obtener todos los servicios activos como DTOs públicos
    public List<ServicioResponse> obtenerServiciosActivos() {
        return servicioRepository.findByActivoTrueOrderByNombre().stream()
                .map(ServicioResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener todos los servicios activos (para uso interno)
    public List<Servicio> obtenerServiciosActivosInternal() {
        return servicioRepository.findByActivoTrueOrderByNombre();
    }

    // Obtener servicio por ID como DTO público
    public ServicioResponse obtenerServicioPorId(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return ServicioResponse.from(servicio);
    }

    // Obtener servicio por ID (para uso interno)
    public Servicio obtenerServicioPorIdInternal(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    // Buscar servicios por nombre como DTOs públicos
    public List<ServicioResponse> buscarPorNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre).stream()
                .map(ServicioResponse::from)
                .collect(Collectors.toList());
    }

    // Buscar servicios por rango de precio como DTOs públicos
    public List<ServicioResponse> buscarPorRangoPrecio(Double precioMin, Double precioMax) {
        return servicioRepository.findByRangoPrecio(precioMin, precioMax).stream()
                .map(ServicioResponse::from)
                .collect(Collectors.toList());
    }
}