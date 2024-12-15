package mx.unam.dgtic.proyecto_final.system.service.corteCaja;

import mx.unam.dgtic.proyecto_final.system.model.entities.CorteCaja;
import mx.unam.dgtic.proyecto_final.system.repository.CorteCajaRepository;
import mx.unam.dgtic.proyecto_final.system.service.venta.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * I M P O R T A N T E
 * Se considera que solo existe una caja,
 * por lo que solo puede haber una sesión activa en el momento
 * de calcular los cortes de caja.
 */
@Service
public class CorteCajaServiceImpl implements CorteCajaService{
    @Autowired
    CorteCajaRepository corteCajaRepository;
    @Autowired
    VentaService ventaService;

    @Override
    public Page<CorteCaja> buscarCorteCaja(Pageable pageable) {
        return corteCajaRepository.findAll(pageable);
    }

    @Override
    public List<CorteCaja> buscarCorteCaja() {
        return corteCajaRepository.findAll();
    }

    @Override
    public CorteCaja buscarCorteCajaId(Integer id) {
        Optional<CorteCaja> op = corteCajaRepository.findById(id);
        return op.orElse(null);
    }

    @Override
    public void guardar(CorteCaja corteCaja) {
        corteCajaRepository.save(corteCaja);
    }

    @Override
    public Integer obtenerUltimoCorteCaja() {
        CorteCaja ultimoCorteCaja = corteCajaRepository.findFirstByOrderByIdCorteCajaDesc();
        if(ultimoCorteCaja == null){
            return 0; //No hay corte caja
        }
        return ultimoCorteCaja.getIdCorteCaja();
    }

    @Override
    public BigDecimal calcularSaldoInicial() {
        // Obtener el último corteCaja
        CorteCaja ultimoCorteCaja = corteCajaRepository.findFirstByOrderByIdCorteCajaDesc();
        //El saldo inicial del corte de caja debe ser igual que el efectivo remanente del corte anterior
        //En el caso del primer corte es 0.0 hasta que lo modifiquen xd
        return (ultimoCorteCaja != null) ? ultimoCorteCaja.getEfectivoRemanente() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calcularTotalVendido(LocalDateTime fechaHoraActual) {
        // Obtener el último corteCaja
        CorteCaja ultimoCorteCaja = corteCajaRepository.findFirstByOrderByIdCorteCajaDesc();
        //Manejar caso en el que es el primer corte y posteriores
        LocalDateTime fechaInicio = (ultimoCorteCaja != null) ? ultimoCorteCaja.getFechaHoraCorte() : ventaService.obtenerFechaHoraPrimeraVenta();
        //obtener el total de ventas con efectivo en el periodo entre el ultimo corte y la fecha actual
        return ventaService.obtenerTotalVentasByTipoPago("FINALIZADA", "E", fechaInicio, fechaHoraActual);
    }

    @Override
    public boolean esPrimerCorte() {
        return corteCajaRepository.findFirstByOrderByIdCorteCajaDesc() == null;
    }

    @Override
    public CorteCaja prepararCorteCaja(LocalDateTime fechaHoraActual, BigDecimal importeEntregado, BigDecimal efectivoRemanente) {
        CorteCaja nuevoCorte = new CorteCaja();
        nuevoCorte.setFechaHoraCorte(fechaHoraActual);
        nuevoCorte.setSaldoInicial(calcularSaldoInicial());
        nuevoCorte.setTotalVentas(calcularTotalVendido(fechaHoraActual));
        nuevoCorte.setImporteEntregado(importeEntregado);
        nuevoCorte.setEfectivoRemanente(efectivoRemanente);
        return nuevoCorte;
    }

    @Override
    public Page<CorteCaja> obtenerCorteCajaByPeriodo(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Pageable pageable) {
        return corteCajaRepository.findAllByFechaHoraCorteBetween(fechaInicial,fechaFinal, pageable);
    }

    @Override
    public Page<CorteCaja> obtenerCorteCajaByPeriodoAndIdCajero(LocalDateTime fechaInicial, LocalDateTime fechaFinal, Integer idCajero, Pageable pageable) {
        return corteCajaRepository.findAllByFechaHoraCorteBetweenAndCajeroIdUsuario(fechaInicial, fechaFinal, idCajero,pageable);
    }

    @Override
    public BigDecimal obtenerPerdidas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        return corteCajaRepository.obtenerSumaDiferenciaImporteYTotalVentas(fechaInicial, fechaFinal);
    }
}
