package mx.unam.dgtic.proyecto_final.system.service.producto;

import jakarta.transaction.Transactional;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService{
    @Autowired
    ProductoRepository productoRepository;

    @Override
    public Page<Producto> buscarProducto(Pageable pageable) {
        return productoRepository.findByProdStatus("ACTIVO", pageable);
    }


    @Override
    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void darDeBaja(String id) {
        // Buscar el producto por ID
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto con ID " + id + " no existe."));

        // Cambiar el estatus a "INACTIVO"
        producto.setProdStatus("INACTIVO");

        // Guardar los cambios
        productoRepository.save(producto);
    }

    @Override
    public Producto buscarProductoId(String id) {
        Optional<Producto> op = productoRepository.findById(id);
        return op.orElse(null);
    }

    @Override
    public long contarProductos() {
        return productoRepository.countByProdStatus("ACTIVO");
    }

    @Override
    public long contarProductosPorCategoria(Long idCategoria) {
        return productoRepository.countByCategoriaIdCategoriaAndProdStatus(idCategoria, "ACTIVO");
    }

    @Override
    public Page<Producto> buscarProductoPorCategoria(Long categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaIdCategoriaAndProdStatus(categoriaId, "ACTIVO", pageable);
    }

    // Método para actualizar el stock de un producto
    @Override
    @Transactional
    public void actualizarStockProducto(String productoId, int cantidad, String operacion) {
        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();

            // Determinar la operación a realizar
            if ("DISMINUIR".equalsIgnoreCase(operacion)) {
                // Reducir el stock
                int nuevoStock = producto.getStock() - cantidad;
                if (nuevoStock < 0) {
                    throw new IllegalArgumentException("No hay suficiente stock para realizar esta operación.");
                }
                producto.setStock(nuevoStock);
            } else if ("AUMENTAR".equalsIgnoreCase(operacion)) {
                // Aumentar el stock
                int nuevoStock = producto.getStock() + cantidad;
                producto.setStock(nuevoStock);
            } else {
                throw new IllegalArgumentException("Operación inválida. Use 'AUMENTAR' o 'DISMINUIR'.");
            }

            productoRepository.save(producto);
        } else {
            throw new IllegalArgumentException("Producto con ID " + productoId + " no encontrado");
        }
    }


    @Override
    public void validarStock(Producto producto, int cantidad) throws Exception {
        if (producto.getStock() < cantidad) {
            throw new Exception("Stock insuficiente");
        }
    }

    @Override
    public BigDecimal calcularTotalProducto(Producto producto, int cantidad) {
        return producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
    }

    //Reportes
    @Override
    public Page<Producto> obtenerProductosConExistenciasBajas(Pageable pageable) {
        return productoRepository.findProductosConExistenciasBajas(pageable);
    }

    @Override
    public Page<Producto> obtenerProductosAgotados(Pageable pageable) {
        return productoRepository.findProductosAgotados(pageable);
    }

    @Override
    public long contarProductosConExistenciasBajas() {
        return productoRepository.contarProductosConExistenciasBajas();
    }

    @Override
    public long contarProductosAgotados() {
        return productoRepository.contarProductosAgotados();
    }

    @Override
    public long contarProductosConExistenciasBajasPorCategoria(Long categoriaId) {
        return productoRepository.contarProductosConExistenciasBajasPorCategoria(categoriaId);
    }

    @Override
    public long contarProductosAgotadosPorCategoria(Long categoriaId) {
        return productoRepository.contarProductosAgotadosPorCategoria(categoriaId);
}

    @Override
    public Page<Producto> obtenerProductosConExistenciasBajasPorCategoria(Long categoriaId, Pageable pageable) {
        return productoRepository.findProductosConExistenciasBajasPorCategoria(categoriaId, pageable);
    }

    @Override
    public Page<Producto> obtenerProductosAgotadosPorCategoria(Long categoriaId, Pageable pageable) {
        return productoRepository.findProductosAgotadosPorCategoria(categoriaId, pageable);
    }

}
