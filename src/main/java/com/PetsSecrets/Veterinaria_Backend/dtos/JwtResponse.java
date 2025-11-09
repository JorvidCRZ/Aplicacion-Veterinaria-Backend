package com.PetsSecrets.Veterinaria_Backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private String rol;
    
    public JwtResponse(String accessToken, Usuario usuario) {
        this.token = accessToken;
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.telefono = usuario.getTelefono();
        this.rol = usuario.getRol().name();
    }
}