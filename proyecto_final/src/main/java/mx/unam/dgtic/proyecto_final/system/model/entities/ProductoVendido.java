package mx.unam.dgtic.proyecto_final.system.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="producto_vendido")
public class ProductoVendido {
    @Id
    @Column(name="id_producto_vendido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProductoVendido;

    private Integer cantidad;

    @Column(name="precio_unitario")
    private BigDecimal precioUnitario;

    @Column(name="costo_unitario")
    private BigDecimal costoUnitario;

    @Column(name="total_producto", insertable = false, updatable = false)
    private BigDecimal totalProducto;//Columna generada

    @Column(name="total_ganancia", insertable = false, updatable = false)
    private BigDecimal totalGanancia; //Columna generada

    @Column(name= "pv_status")
    private String pvStatus;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    //FK Producto
    @ManyToOne(targetEntity = Producto.class)
    @JoinColumn(name = "codigo")
    private Producto producto;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    //FK Venta
    @ManyToOne(targetEntity = Venta.class)
    @JoinColumn(name="id_venta")
    private Venta venta;

    public ProductoVendido(Integer cantidad, BigDecimal precioUnitario, BigDecimal costoUnitario) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.costoUnitario = costoUnitario;
    }

    public ProductoVendido(Integer cantidad, BigDecimal precioUnitario, BigDecimal costoUnitario, BigDecimal totalProducto, BigDecimal totalGanancia, String pvStatus) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.costoUnitario = costoUnitario;
        this.totalProducto = totalProducto;
        this.totalGanancia = totalGanancia;
        this.pvStatus = pvStatus;
    }

    public ProductoVendido(Integer cantidad, BigDecimal precioUnitario, BigDecimal costoUnitario, Producto producto, Venta venta) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.costoUnitario = costoUnitario;
        this.producto = producto;
        this.venta = venta;
    }

    public ProductoVendido(Integer idProductoVendido, Integer cantidad, BigDecimal precioUnitario, BigDecimal costoUnitario, BigDecimal totalProducto, BigDecimal totalGanancia, String pvStatus) {
        this.idProductoVendido = idProductoVendido;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.costoUnitario = costoUnitario;
        this.totalProducto = totalProducto;
        this.totalGanancia = totalGanancia;
        this.pvStatus = pvStatus;
    }

}
