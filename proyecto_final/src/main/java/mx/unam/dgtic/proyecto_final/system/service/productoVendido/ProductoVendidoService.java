package mx.unam.dgtic.proyecto_final.system.service.productoVendido;

import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoVendidoService {
    Page<ProductoVendido> buscarProductoVendido(Pageable pageable);
    List<ProductoVendido> buscarProductoVendido();
    ProductoVendido crearProductoVendido(Venta venta, Producto producto, int cantidad, BigDecimal totalProducto);
    ProductoVendido guardar(ProductoVendido productoVendido);
    ProductoVendido buscarProductoVendidoId(Integer id);

    /* V E N T A */
    //Lista de productos vendidos asociados a una venta
    List<ProductoVendido> getProductosVendidosByVenta(Integer idVenta);

    //Obtener productos 'VENDIDOS' por venta
    List<ProductoVendido> obtenerProductosVendidosPorVenta(Integer idVenta);

    ProductoVendido cancelarProductoVendido(ProductoVendido productoVendido);
    ProductoVendido buscarProductoVendidoByCodigoAndIdVenta(Integer idVenta, String codigo);

    void cancelarProductosVendidosDeVenta(Integer idVenta);
    }
