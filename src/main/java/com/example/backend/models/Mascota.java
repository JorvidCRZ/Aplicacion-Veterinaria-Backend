package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "mascotas")
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "foto_url")
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    private Tipo tipo = Tipo.propia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_adopcion", nullable = false)
    private EstadoAdopcion estadoAdopcion = EstadoAdopcion.disponible;

    @Column(name = "vacunado")
    private Boolean vacunado = false;

    @Column(name = "esterilizado")
    private Boolean esterilizado = false;

    @Column(name = "bueno_con_ninos")
    private Boolean buenoConNinos = false;

    @Column(name = "bueno_con_otras_mascotas")
    private Boolean buenoConOtrasMascotas = false;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_adopcion")
    private LocalDate fechaAdopcion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Boolean activo = true;

    public enum Especie { perro, gato, conejo, ave, otro }
    public enum Genero { macho, hembra }
    public enum Tamano { pequeño, mediano, grande }
    public enum Tipo { propia, adopcion }
    public enum EstadoAdopcion { disponible, adoptado, en_proceso, no_disponible }
}
