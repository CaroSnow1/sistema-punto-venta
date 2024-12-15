package mx.unam.dgtic.proyecto_final.system.model.entities;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_venta")
    private Integer idVenta;

    @NotNull(message = "El total de la venta no puede ser nulo")
    //@DecimalMin(value = "0.0", inclusive = false, message = "El total de la venta debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El total de la venta debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name="total_venta")
    private BigDecimal totalVenta;

    @NotNull(message = "El importe pagado no puede ser nulo")
    //@DecimalMin(value = "0.0", inclusive = false, message = "El importe pagado debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El importe pagado debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "importe_pagado")
    private BigDecimal importePagado;

    @DecimalMin(value = "0.0", inclusive = true, message = "El cambio entregado no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El cambio entregado debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "cambio_entregado")
    private BigDecimal cambioEntregado;

    @NotNull(message = "El status de la venta no puede ser nulo")
    @Column(name="venta_status")
    private String ventaStatus;

    @NotNull(message = "La fecha y hora de la venta no puede ser nula")
    @Column(name="fecha_hora_venta")
    private LocalDateTime fechaHoraVenta;

    @NotNull(message = "El tipo de pago no puede ser nulo")
    @Column(name="tipo_pago", length = 1)
    private String tipoPago;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    //FK Usuario (Cajero)
    @ManyToOne(targetEntity = Usuario.class)
    @JoinColumn(name="id_cajero")
    private Usuario cajero;

    public Venta(BigDecimal totalVenta, BigDecimal importePagado, BigDecimal cambioEntregado, String ventaStatus, LocalDateTime fechaHoraVenta, String tipoPago) {
        this.totalVenta = totalVenta;
        this.importePagado = importePagado;
        this.cambioEntregado = cambioEntregado;
        this.ventaStatus = ventaStatus;
        this.fechaHoraVenta = fechaHoraVenta;
        this.tipoPago = tipoPago;
    }

    public Venta(Integer idVenta, BigDecimal totalVenta, BigDecimal importePagado, BigDecimal cambioEntregado, String ventaStatus, LocalDateTime fechaHoraVenta, String tipoPago) {
        this.idVenta = idVenta;
        this.totalVenta = totalVenta;
        this.importePagado = importePagado;
        this.cambioEntregado = cambioEntregado;
        this.ventaStatus = ventaStatus;
        this.fechaHoraVenta = fechaHoraVenta;
        this.tipoPago = tipoPago;
    }

}
