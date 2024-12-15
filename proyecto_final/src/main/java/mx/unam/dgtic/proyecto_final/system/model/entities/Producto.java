package mx.unam.dgtic.proyecto_final.system.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@NamedQuery(
        name = "Producto.findAllOrderByProfitDesc",
        query = "SELECT p, (p.precioVenta - p.costoCompra) AS profit FROM Producto p ORDER BY profit DESC"
)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="producto")
public class Producto {
    @Id
    @NotBlank(message = "El código no debe estar vacío")  // Validación de no vacío y sin espacios en blanco
    private String codigo;

    @NotBlank(message = "El nombre no debe estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;

    //@NotBlank(message = "El estatus no puede ser nulo")
    //@Pattern(regexp = "ACTIVO|INACTIVO", message = "El status debe ser 'ACTIVO' o 'INACTIVO'")
    @Column(name="prod_status")
    private String prodStatus;

    @NotNull(message = "El costo de compra no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo de compra debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El costo de compra debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name="costo_compra")
    private BigDecimal costoCompra;

    @NotNull(message = "El precio de venta no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "El precio de venta debe ser un número válido con hasta 10 dígitos enteros y 2 decimales")
    @Column(name="precio_venta")
    private BigDecimal precioVenta;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    //FK Categoria
    @ManyToOne(targetEntity = Categoria.class)
    @JoinColumn(name = "id_categoria")
    @NotNull(message = "La categoría no puede ser nula")  // Validación para que la categoría no sea nula
    private Categoria categoria;

    public Producto(String codigo, String nombre, Integer stock, String prodStatus, BigDecimal costoCompra, BigDecimal precioVenta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.prodStatus = prodStatus;
        this.costoCompra = costoCompra;
        this.precioVenta = precioVenta;
    }

}
