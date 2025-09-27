package com.example.backend.services;

import com.example.backend.dto.ReporteCitaDTO;
import com.example.backend.models.Cita;
import com.example.backend.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    public List<ReporteCitaDTO> obtenerReporte(LocalDate inicio, LocalDate fin) {
        // 1. Traer todas las citas en el rango
        List<Cita> citas = citaRepository.findByFechaBetween(inicio, fin);

        // 2. Agrupar por veterinario y contar
        Map<String, Long> conteo = citas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getServicio().getVeterinario().getNombreCompleto(),
                        Collectors.counting()
                ));

        // 3. Convertir a DTO
        return conteo.entrySet().stream()
                .map(e -> new ReporteCitaDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}