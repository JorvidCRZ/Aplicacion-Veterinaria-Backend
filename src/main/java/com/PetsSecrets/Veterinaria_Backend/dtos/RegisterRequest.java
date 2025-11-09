package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombreCompleto;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefono;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    private String password;
    
    // Campo opcional para especificar rol (usado por admin)
    // Si no se proporciona, por defecto será "USER"
    private String rol;
}