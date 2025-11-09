package com.PetsSecrets.Veterinaria_Backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "mascotas")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private Especie especie;

    @Column(name = "raza")
    private String raza;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @Column(name = "edad")
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano")
    private Tamano tamano;

    @Column(name = "descripcion")
    private String descripcion;

    @Lob
    @Column(name = "foto_url", columnDefinition = "LONGTEXT")
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Tipo tipo = Tipo.propia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_adopcion", nullable = false)
    @Builder.Default
    private EstadoAdopcion estadoAdopcion = EstadoAdopcion.disponible;

    @Column(name = "vacunado")
    @Builder.Default
    private Boolean vacunado = false;

    @Column(name = "esterilizado")
    @Builder.Default
    private Boolean esterilizado = false;

    @Column(name = "bueno_con_ninos")
    @Builder.Default
    private Boolean buenoConNinos = false;

    @Column(name = "bueno_con_otras_mascotas")
    @Builder.Default
    private Boolean buenoConOtrasMascotas = false;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_adopcion")
    private LocalDate fechaAdopcion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación con adopciones (una mascota puede tener muchas solicitudes de adopción)
    @OneToMany(mappedBy = "mascota", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Adopcion> adopciones;

    @Builder.Default
    private Boolean activo = true;

    public enum Especie { perro, gato, conejo, ave, otro }
    public enum Genero { macho, hembra }
    public enum Tamano { pequeño, mediano, grande }
    public enum Tipo { propia, adopcion }
    public enum EstadoAdopcion { disponible, adoptado, en_proceso, no_disponible }
}
