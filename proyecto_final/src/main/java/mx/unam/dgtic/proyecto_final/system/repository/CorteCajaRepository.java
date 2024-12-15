package mx.unam.dgtic.proyecto_final.system.repository;

import mx.unam.dgtic.proyecto_final.system.model.entities.CorteCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CorteCajaRepository extends JpaRepository<CorteCaja, Integer> {

    //Obtener el último corteCaja por ID
    CorteCaja findFirstByOrderByIdCorteCajaDesc();

    //Obtener Cortes de caja por periodo
    Page<CorteCaja> findAllByFechaHoraCorteBetween(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Pageable pageable);

    //Obtener cortes de caja por periodo asociados a un cajero
    Page<CorteCaja> findAllByFechaHoraCorteBetweenAndCajeroIdUsuario(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Integer idCajero, Pageable pageable);

    @Query("SELECT SUM(c.importeEntregado - c.totalVentas) " +
            "FROM CorteCaja c " +
            "WHERE c.fechaHoraCorte BETWEEN :fechaHoraInicial AND :fechaHoraFinal")
    BigDecimal obtenerSumaDiferenciaImporteYTotalVentas(
            @Param("fechaHoraInicial") LocalDateTime fechaHoraInicial,
            @Param("fechaHoraFinal") LocalDateTime fechaHoraFinal);

}
