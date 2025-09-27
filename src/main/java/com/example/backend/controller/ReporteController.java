package com.example.backend.controller;

import com.example.backend.dto.ReporteCitaDTO;
import com.example.backend.services.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final CitaService citaService;

    @GetMapping("/citas")
    public List<ReporteCitaDTO> reporteCitas(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return citaService.obtenerReporte(inicio, fin);
    }
}


