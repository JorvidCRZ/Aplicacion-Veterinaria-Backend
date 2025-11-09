package com.PetsSecrets.Veterinaria_Backend.controllers;

import com.PetsSecrets.Veterinaria_Backend.dtos.*;
import com.PetsSecrets.Veterinaria_Backend.jwt.JwtUtils;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Verificar credenciales
            Optional<Usuario> usuario = usuarioService.autenticarUsuario(loginRequest);
            
            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Credenciales inválidas"));
            }

            // Generar token JWT
            String jwt = jwtUtils.generateJwtToken(usuario.get().getEmail());
            
            // Retornar respuesta con token y datos del usuario
            JwtResponse response = new JwtResponse(jwt, usuario.get());
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            usuarioService.registrarUsuario(signUpRequest);
            
            return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtils.validateJwtToken(token)) {
                    String email = jwtUtils.getEmailFromJwtToken(token);
                    Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
                    
                    if (usuario.isPresent()) {
                        JwtResponse response = new JwtResponse(token, usuario.get());
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token inválido"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token inválido"));
        }
    }
}