package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteCitaDTO {
    private String doctor;
    private Long totalCitas;
}