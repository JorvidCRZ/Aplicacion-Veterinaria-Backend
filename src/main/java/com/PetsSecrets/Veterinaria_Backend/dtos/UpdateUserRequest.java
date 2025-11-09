package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;

@Data
public class UpdateUserRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombreCompleto;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefono;
    
    private Usuario.Rol rol = Usuario.Rol.usuario;
    
    // Contraseña opcional para actualización
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    private String password;
}