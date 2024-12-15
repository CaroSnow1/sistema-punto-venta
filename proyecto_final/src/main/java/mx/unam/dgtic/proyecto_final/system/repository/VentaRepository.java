package mx.unam.dgtic.proyecto_final.system.repository;

import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    //Suma toal de las ventas 'finalizadas' realizadas en un periodo por tipo de pago
    @Query("SELECT SUM(v.totalVenta) " +
            "FROM Venta v " +
            "WHERE v.ventaStatus = :ventaStatus " +
            "AND v.tipoPago = :tipoPago " +
            "AND v.fechaHoraVenta BETWEEN :fechaHoraInicial AND :fechaHoraFinal")
    BigDecimal obtenerSumaTotalVenta(
            @Param("ventaStatus") String ventaStatus,
            @Param("tipoPago") String tipoPago,
            @Param("fechaHoraInicial") LocalDateTime fechaHoraInicial,
            @Param("fechaHoraFinal") LocalDateTime fechaHoraFinal
    );

    //Suma total de ganancias de ventas realizadas en un periodo con tipo de pago Efectivo
    @Query("SELECT SUM(pv.totalGanancia) " +
            "FROM Venta v " +
            "JOIN ProductoVendido pv ON v.idVenta = pv.venta.idVenta " +
            "WHERE v.fechaHoraVenta BETWEEN :fechaHoraVentaInicial AND :fechaHoraVentaFinal " +
            "AND v.ventaStatus = 'FINALIZADA' " +
            "AND v.tipoPago = 'E' " +
            "AND pv.pvStatus = 'VENDIDO'")
    BigDecimal calcularGanancias(
            @Param("fechaHoraVentaInicial") LocalDateTime fechaHoraVentaInicial,
            @Param("fechaHoraVentaFinal") LocalDateTime fechaHoraVentaFinal);


    //Obtener total de venta por id
    @Query("SELECT v.totalVenta FROM Venta v WHERE v.id = :idVenta")
    BigDecimal obtenerTotalVentaPorId(@Param("idVenta") Long idVenta);

    // Consulta derivada para obtener la última venta
    Venta findFirstByOrderByFechaHoraVentaDesc();

    //Obtener la fecha de la primera
    Venta findFirstByOrderByIdVentaAsc();

    @Query("SELECT v FROM Venta v WHERE v.fechaHoraVenta BETWEEN :fechaInicio AND :fechaFin AND v.ventaStatus = :ventaStatus")
    List<Venta> findVentasByFechaHoraVentaBetweenAndVentaStatus(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin, @Param("ventaStatus") String ventaStatus);

    @Query("SELECT v FROM Venta v WHERE v.fechaHoraVenta BETWEEN :fechaInicio AND :fechaFin AND v.tipoPago = :tipoPago AND v.ventaStatus = 'FINALIZADA'")
    Page<Venta> findVentasByFechaHoraVentaBetweenAndTipoPago(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin, @Param("tipoPago") String tipoPago, Pageable pageable);

    @Query("SELECT SUM(pv.totalGanancia) " +
            "FROM Venta v JOIN ProductoVendido pv ON v.idVenta = pv.venta.idVenta " +
            "WHERE v.fechaHoraVenta BETWEEN :fechaInicio AND :fechaFin AND v.tipoPago = :tipoPago AND v.ventaStatus = 'FINALIZADA'")
    BigDecimal calcularGanancias(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin, @Param("tipoPago") String tipoPago);

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaHoraVenta BETWEEN :fechaInicio AND :fechaFin AND v.tipoPago = :tipoPago AND v.ventaStatus = 'FINALIZADA'")
    BigDecimal obtenerSumaVentasPorTipoPago(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin, @Param("tipoPago") String tipoPago);


}
