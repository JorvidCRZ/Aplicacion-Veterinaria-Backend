package com.PetsSecrets.Veterinaria_Backend.services;

import com.PetsSecrets.Veterinaria_Backend.dtos.LoginRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.RegisterRequest;
import com.PetsSecrets.Veterinaria_Backend.dtos.UpdateUserRequest;
import com.PetsSecrets.Veterinaria_Backend.models.Usuario;
import com.PetsSecrets.Veterinaria_Backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Usuario registrarUsuario(RegisterRequest registerRequest) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }
        
        // Determinar el rol a usar
        Usuario.Rol rolUsuario = Usuario.Rol.usuario; // Valor por defecto
        
        // Si se proporciona un rol (para creación desde admin), usarlo
        if (registerRequest.getRol() != null && !registerRequest.getRol().isEmpty()) {
            try {
                rolUsuario = Usuario.Rol.valueOf(registerRequest.getRol().toLowerCase());
            } catch (IllegalArgumentException e) {
                // Si el rol no es válido, usar el valor por defecto
                System.out.println("Rol inválido proporcionado: " + registerRequest.getRol() + ", usando 'usuario' por defecto");
            }
        }
        
        // Crear nuevo usuario
        Usuario usuario = Usuario.builder()
                .nombreCompleto(registerRequest.getNombreCompleto())
                .email(registerRequest.getEmail())
                .telefono(registerRequest.getTelefono())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .rol(rolUsuario)
                .fechaRegistro(LocalDateTime.now())
                .build();
        
        return usuarioRepository.save(usuario);
    }
    
    public Optional<Usuario> autenticarUsuario(LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginRequest.getEmail());
        
        if (usuario.isPresent() && 
            passwordEncoder.matches(loginRequest.getPassword(), usuario.get().getPasswordHash())) {
            return usuario;
        }
        
        return Optional.empty();
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public void crearUsuarioAdminSiNoExiste() {
        String adminEmail = "admin@admin.com";
        
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nombreCompleto("Administrador")
                    .email(adminEmail)
                    .telefono("999999999")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .rol(Usuario.Rol.admin)
                    .fechaRegistro(LocalDateTime.now())
                    .build();
            
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado por defecto");
        }
    }
    
    // ============= MÉTODOS CRUD =============
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Usuario actualizarUsuario(Integer id, UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Verificar si el email ya existe en otro usuario
        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }
        
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(request.getRol());
        
        // Solo actualizar contraseña si se proporciona
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        
        return usuarioRepository.save(usuario);
    }
    
    public void eliminarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Prevenir eliminación del admin principal
        if ("admin@admin.com".equals(usuario.getEmail())) {
            throw new RuntimeException("No se puede eliminar el usuario administrador principal");
        }
        
        usuarioRepository.delete(usuario);
    }
}