package mx.unam.dgtic.proyecto_final.system.service.producto;

import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    Page<Producto> buscarProducto(Pageable pageable);

    void guardar(Producto producto);
    void darDeBaja(String id);
    Producto buscarProductoId(String id);

    long contarProductos();
    long contarProductosPorCategoria(Long idCategoria);

    Page<Producto> buscarProductoPorCategoria(Long categoriaId, Pageable pageable);

    void actualizarStockProducto(String productoId, int cantidad, String operacion);
    void validarStock(Producto producto, int cantidad) throws Exception;
    BigDecimal calcularTotalProducto(Producto producto, int cantidad);

    //Reporrtes
    Page<Producto> obtenerProductosConExistenciasBajas(Pageable pageable);

    Page<Producto> obtenerProductosAgotados(Pageable pageable);

    long contarProductosConExistenciasBajas();

    long contarProductosAgotados();

    long contarProductosConExistenciasBajasPorCategoria(Long categoriaId);

    long contarProductosAgotadosPorCategoria(Long categoriaId);

    Page<Producto> obtenerProductosConExistenciasBajasPorCategoria(Long categoriaId, Pageable pageable);

    Page<Producto> obtenerProductosAgotadosPorCategoria(Long categoriaId, Pageable pageable);
}



