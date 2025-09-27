package com.example.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FechaUtils {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatear(LocalDate fecha) {
        return fecha.format(FORMATO);
    }
}
