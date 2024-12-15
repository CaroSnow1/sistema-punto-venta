package mx.unam.dgtic.proyecto_final.system.service.venta;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    Page<Venta> buscarVenta(Pageable pageable);
    List<Venta> buscarVenta();

    Venta guardarVenta(Venta venta);

    Venta crearVentaDefault(Usuario cajero);
    void agregarProductoAVenta(Venta venta, Producto producto, int cantidad) throws Exception;
    void finalizarVenta(Venta venta, BigDecimal importePagado);
    BigDecimal obtenerTotalVenta(Long idVenta);

    BigDecimal obtenerTotalVentasByTipoPago(String ventaStatus, String tipoPago, LocalDateTime fechaInicial, LocalDateTime fechaFinal);
    BigDecimal obtenerGananciasEfectivo(LocalDateTime fechaInicial, LocalDateTime fechaFinal);

    Venta buscarVentaId(Integer id);
    Venta obtenerUltimaVenta();
    LocalDateTime obtenerFechaHoraPrimeraVenta();

    void cancelarVenta(Venta venta, String usernameGerente, String passwordGerente);

    ProductoVendido cancelarProducto(Venta venta, String codigo,  String usernameGerente, String passwordGerente);
    List<ProductoVendido> getProductosVendidos(Integer idVenta);

    List<Venta> obtenerVentasPorRango(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Page<Venta> obtenerVentasPorRangoYTipoPago(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago, Pageable pageable);

    BigDecimal calcularSumaVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago);

    BigDecimal calcularGananciasVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago);
}
