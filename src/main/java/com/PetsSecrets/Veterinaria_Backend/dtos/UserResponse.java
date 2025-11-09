package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.Data;
import lombok.AllArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String rol;
    private LocalDateTime fechaRegistro;
    
    public UserResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.email = usuario.getEmail();
        this.telefono = usuario.getTelefono();
        this.rol = usuario.getRol().name();
        this.fechaRegistro = usuario.getFechaRegistro();
    }
}