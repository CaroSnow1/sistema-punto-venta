package mx.unam.dgtic.proyecto_final.system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private String fechaHoraVenta; // Fecha formateada
    private BigDecimal totalVenta;
    private String tipoPago;
    private String nombreCajero;
}
