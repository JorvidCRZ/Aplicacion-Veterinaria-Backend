package com.PetsSecrets.Veterinaria_Backend.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol = Rol.usuario;

    @Builder.Default
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Relación con mascotas (un usuario puede tener muchas mascotas)
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Mascota> mascotas;

    // Relación con adopciones (un usuario puede tener muchas solicitudes de adopción)
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Adopcion> adopciones;

    public enum Rol {
        usuario,
        admin
    }

    // Método auxiliar para obtener nombre (para compatibilidad)
    public String getNombre() {
        return this.nombreCompleto;
    }
}
