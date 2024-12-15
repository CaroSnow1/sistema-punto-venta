package mx.unam.dgtic.proyecto_final.system.service.corteCaja;

import mx.unam.dgtic.proyecto_final.system.model.entities.CorteCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CorteCajaService {
    Page<CorteCaja> buscarCorteCaja(Pageable pageable);
    List<CorteCaja> buscarCorteCaja();
    CorteCaja buscarCorteCajaId(Integer id);
    Integer obtenerUltimoCorteCaja();
    //void crearCorteCaja();
    void guardar(CorteCaja corteCaja);
    BigDecimal calcularSaldoInicial();
    BigDecimal calcularTotalVendido(LocalDateTime fechaHoraActual);

    boolean esPrimerCorte();

    CorteCaja prepararCorteCaja(LocalDateTime fechaHoraActual, BigDecimal importeEntregado, BigDecimal efectivoRemanente);

    Page<CorteCaja> obtenerCorteCajaByPeriodo(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Pageable pageable);
    Page<CorteCaja> obtenerCorteCajaByPeriodoAndIdCajero(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Integer idCajero, Pageable pageable);

    BigDecimal obtenerPerdidas(LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}
