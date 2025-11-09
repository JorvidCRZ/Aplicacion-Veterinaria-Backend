package com.PetsSecrets.Veterinaria_Backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "adopciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @Builder.Default
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAdopcion estado = EstadoAdopcion.pendiente;

    @Enumerated(EnumType.STRING)
    @Column(name = "experiencia_mascotas", nullable = false)
    private ExperienciaMascotas experienciaMascotas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vivienda", nullable = false)
    private TipoVivienda tipoVivienda;

    @Column(name = "otras_mascotas")
    private String otrasMascotas;

    @Column(name = "horario_trabajo")
    private String horarioTrabajo;

    @Column(name = "motivo_adopcion")
    private String motivoAdopcion;

    @Column(name = "contacto_emergencia", nullable = false)
    private String contactoEmergencia;

    @Column(name = "veterinario_referencia")
    private String veterinarioReferencia;

    @Builder.Default
    @Column(name = "acepta_condiciones")
    private Boolean aceptaCondiciones = false;

    @Builder.Default
    @Column(name = "acepta_visita")
    private Boolean aceptaVisita = false;

    public enum EstadoAdopcion { pendiente,aprobada,rechazada,completada }
    public enum ExperienciaMascotas { nula,básica,intermedia,avanzada}
    public enum TipoVivienda { casa,departamento,finca,otro }
}
