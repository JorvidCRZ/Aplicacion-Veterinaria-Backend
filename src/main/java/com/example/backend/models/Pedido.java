package com.example.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado = EstadoPedido.pendiente;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "envio", nullable = false)
    private BigDecimal envio = BigDecimal.ZERO;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPago metodoPago;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles;

    public enum EstadoPedido {
        pendiente,procesando,enviado,entregado,cancelado
    }

    public enum MetodoPago {
        tarjeta,
        yape
    }
}
