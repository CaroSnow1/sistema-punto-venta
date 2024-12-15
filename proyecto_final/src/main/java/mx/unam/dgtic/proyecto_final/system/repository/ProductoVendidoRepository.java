package mx.unam.dgtic.proyecto_final.system.repository;

import jakarta.transaction.Transactional;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoVendidoRepository extends JpaRepository<ProductoVendido, Integer> {
    List<ProductoVendido> findByVentaIdVenta(Integer idVenta);

    // Consulta derivada para obtener productos vendidos por idVenta
    List<ProductoVendido> findByVenta_IdVenta(Integer idVenta);

    ProductoVendido findByVentaIdVentaAndProductoCodigoAndPvStatus(Integer idVenta, String codigo, String pvStatus);

    // Método para buscar productos vendidos 'VENDIDOS' asociados a una venta
    List<ProductoVendido> findByVenta_IdVentaAndPvStatus(Integer idVenta, String pvStatus);

    //Modificar todos los productos vendidos a 'CANCELADO' asociados a una venta cancelada
    @Modifying
    @Query("UPDATE ProductoVendido pv SET pv.pvStatus = 'CANCELADO' WHERE pv.venta.idVenta = :idVenta")
    int cancelarProductosDeUnaVenta(Integer idVenta);
}
