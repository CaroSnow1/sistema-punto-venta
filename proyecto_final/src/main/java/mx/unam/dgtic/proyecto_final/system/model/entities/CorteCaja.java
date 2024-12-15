package mx.unam.dgtic.proyecto_final.system.model.entities;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="corte_caja")
public class CorteCaja {
    @Id
    @Column(name="id_corte_caja")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCorteCaja;

    @NotNull(message = "El saldo inicial es obligatorio")
    @Digits(integer = 10, fraction = 2, message = "El saldo inicial debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal saldoInicial;

    @NotNull(message = "El total de ventas es obligatorio")
    @Digits(integer = 10, fraction = 2, message = "El total de ventas debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "total_ventas", nullable = false)
    private BigDecimal totalVentas;

    @NotNull(message = "El importe entregado es obligatorio")
    @Digits(integer = 10, fraction = 2, message = "El importe entregado debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "importe_entregado", nullable = false)
    private BigDecimal importeEntregado;

    @NotNull(message = "El efectivo remanente es obligatorio")
    @Digits(integer = 10, fraction = 2, message = "El efectivo remanente debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "efectivo_remanente", nullable = false)
    private BigDecimal efectivoRemanente;

    @NotNull(message = "La fecha y hora del corte es obligatoria")
    @Column(name = "fecha_hora_corte", nullable = false)
    private LocalDateTime fechaHoraCorte;

    @NotNull(message = "El cajero es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cajero", nullable = false)
    @JsonIgnore
    private Usuario cajero;

    @NotNull(message = "El gerente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_gerente", nullable = false)
    @JsonIgnore
    private Usuario gerente;

    public CorteCaja(BigDecimal saldoInicial, BigDecimal totalVentas, BigDecimal importeEntregado, BigDecimal efectivoRemanente, LocalDateTime fechaHoraCorte) {
        this.saldoInicial = saldoInicial;
        this.totalVentas = totalVentas;
        this.importeEntregado = importeEntregado;
        this.efectivoRemanente = efectivoRemanente;
        this.fechaHoraCorte = fechaHoraCorte;
    }

    public CorteCaja(Integer idCorteCaja, BigDecimal totalVentas, BigDecimal importeEntregado, BigDecimal efectivoRemanente, LocalDateTime fechaHoraCorte) {
        this.idCorteCaja = idCorteCaja;
        this.totalVentas = totalVentas;
        this.importeEntregado = importeEntregado;
        this.efectivoRemanente = efectivoRemanente;
        this.fechaHoraCorte = fechaHoraCorte;
    }

    public CorteCaja(BigDecimal saldoInicial, BigDecimal totalVentas, BigDecimal importeEntregado, BigDecimal efectivoRemanente, LocalDateTime fechaHoraCorte, Usuario cajero, Usuario gerente) {
        this.saldoInicial = saldoInicial;
        this.totalVentas = totalVentas;
        this.importeEntregado = importeEntregado;
        this.efectivoRemanente = efectivoRemanente;
        this.fechaHoraCorte = fechaHoraCorte;
        this.cajero = cajero;
        this.gerente = gerente;
    }


}
