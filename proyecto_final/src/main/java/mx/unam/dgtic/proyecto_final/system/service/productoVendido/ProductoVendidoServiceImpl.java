package mx.unam.dgtic.proyecto_final.system.service.productoVendido;

import jakarta.transaction.Transactional;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import mx.unam.dgtic.proyecto_final.system.repository.ProductoVendidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoVendidoServiceImpl implements ProductoVendidoService{
    @Autowired
    ProductoVendidoRepository productoVendidoRepository;

    @Override
    public Page<ProductoVendido> buscarProductoVendido(Pageable pageable) {
        return productoVendidoRepository.findAll(pageable);
    }


    @Override
    public List<ProductoVendido> buscarProductoVendido() {
        return productoVendidoRepository.findAll();
    }

    //Método para crear productos vendidos asociados a una venta
    @Override
    public ProductoVendido crearProductoVendido(Venta venta, Producto producto, int cantidad, BigDecimal totalProducto) {
        ProductoVendido productoVendido = new ProductoVendido();
        productoVendido.setVenta(venta);
        productoVendido.setProducto(producto);
        productoVendido.setCantidad(cantidad);
        productoVendido.setPrecioUnitario(producto.getPrecioVenta()); //Hacemos el precio unitario igual al precio unitario en producto
        productoVendido.setCostoUnitario(producto.getCostoCompra()); //Hacemos el costo unitario igual al costo compra en producto
        productoVendido.setPvStatus("VENDIDO"); //Por default configuramos como estado vendido
        productoVendido.setTotalProducto(totalProducto);

        return productoVendidoRepository.save(productoVendido);
    }

    // Método para guardar un producto vendido asociado a una venta
    @Transactional
    @Override
    public ProductoVendido guardar(ProductoVendido productoVendido) {
        return productoVendidoRepository.save(productoVendido);
    }

    @Override
    public ProductoVendido buscarProductoVendidoId(Integer id) {
        Optional<ProductoVendido> op = productoVendidoRepository.findById(id);
        return op.orElse(null);
    }

    @Override
    public List<ProductoVendido> getProductosVendidosByVenta(Integer idVenta) {
        return productoVendidoRepository.findByVenta_IdVenta(idVenta);
    }

    @Override
    public List<ProductoVendido> obtenerProductosVendidosPorVenta(Integer idVenta) {
        return productoVendidoRepository.findByVenta_IdVentaAndPvStatus(idVenta, "VENDIDO");
    }

    @Override
    public ProductoVendido cancelarProductoVendido(ProductoVendido productoVendido) {
        productoVendido.setPvStatus("CANCELADO");
        productoVendidoRepository.save(productoVendido);
        return productoVendido;
    }

    @Override
    public ProductoVendido buscarProductoVendidoByCodigoAndIdVenta(Integer idVenta, String codigo) {
        return productoVendidoRepository.findByVentaIdVentaAndProductoCodigoAndPvStatus(idVenta, codigo, "VENDIDO");
    }

    @Override
    public void cancelarProductosVendidosDeVenta(Integer idVenta) {
        productoVendidoRepository.cancelarProductosDeUnaVenta(idVenta);
    }


}
