package com.PetsSecrets.Veterinaria_Backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sedes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "ciudad")
    private String ciudad;
}
