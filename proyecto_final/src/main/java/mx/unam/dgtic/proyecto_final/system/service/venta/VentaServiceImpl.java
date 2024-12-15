package mx.unam.dgtic.proyecto_final.system.service.venta;

import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import mx.unam.dgtic.proyecto_final.system.repository.VentaRepository;
import mx.unam.dgtic.proyecto_final.system.service.producto.ProductoService;
import mx.unam.dgtic.proyecto_final.system.service.productoVendido.ProductoVendidoService;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService{

    @Autowired
    VentaRepository ventaRepository;


    @Autowired
    ProductoVendidoService productoVendidoService;
    @Autowired
    ProductoService productoService;
    @Autowired
    UsuarioService usuarioService;

    @Override
    public Page<Venta> buscarVenta(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public List<Venta> buscarVenta() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta guardarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

        /*
    Registrar venta:
    1. Crear la venta
    2. Registrar los productos vendidos asosiados a la venta
    3. Calcular los atributos de venta con los productos vendidos
    4. Setear los atributos
    5. Actualizar stock en Productos
    6. Actualizar la venta
     */

    @Override
    public Venta crearVentaDefault(Usuario cajero) {
        Venta venta = new Venta();
        //Setear datos not null
        venta.setCambioEntregado(BigDecimal.ZERO);
        venta.setImportePagado(BigDecimal.ZERO);
        venta.setTotalVenta(BigDecimal.ZERO);
        venta.setFechaHoraVenta(LocalDateTime.now());
        venta.setTipoPago("E");
        venta.setVentaStatus("CREADA"); //Status de venta default
        venta.setCajero(cajero);// cajero en sesion
        return ventaRepository.save(venta);
    }

    @Override
    public void agregarProductoAVenta(Venta venta, Producto producto, int cantidad) throws Exception {
        // Validar stock
        productoService.validarStock(producto, cantidad);

        // Calcular total del producto vendido
        BigDecimal totalProducto = productoService.calcularTotalProducto(producto, cantidad);

        // Crear el objeto ProductoVendido
        ProductoVendido productoVendido = productoVendidoService.crearProductoVendido(venta, producto, cantidad, totalProducto);

        // Actualizar stock del producto
        productoService.actualizarStockProducto(producto.getCodigo(), cantidad, "DISMINUIR");

        // Actualizar total de la venta
        venta.setTotalVenta(venta.getTotalVenta().add(totalProducto));

        //Modificar el status de la venta
        if(Objects.equals(venta.getVentaStatus(), "CREADA")){
            venta.setVentaStatus("MODIFICADA");
        }

        ventaRepository.save(venta);
    }

    @Override
    public void finalizarVenta(Venta venta, BigDecimal importePagado) {
        // Verificar si la venta tiene productos vendidos asociados
        List<ProductoVendido> productosVendidos = productoVendidoService.obtenerProductosVendidosPorVenta(venta.getIdVenta());

        if (productosVendidos.isEmpty()) {
            throw new IllegalArgumentException("La venta no contiene productos. Agrega productos antes de finalizar la venta.");
        }

        // Verificar si el importe pagado es suficiente
        if (importePagado.compareTo(venta.getTotalVenta()) < 0) {
            throw new IllegalArgumentException("El importe pagado es insuficiente para cubrir el total de la venta.");
        }

        // Asignar el importe pagado y calcular el cambio entregado
        venta.setImportePagado(importePagado);
        venta.setCambioEntregado(importePagado.subtract(venta.getTotalVenta()));

        //Cambiar status de la venta
        venta.setVentaStatus("FINALIZADA");

        // Guardar la venta actualizada en el repositorio
        ventaRepository.save(venta);

    }

    @Override
    public BigDecimal obtenerTotalVenta(Long idVenta) {
        return ventaRepository.obtenerTotalVentaPorId(idVenta);
    }

    @Override
    public BigDecimal obtenerTotalVentasByTipoPago(String ventaStatus, String tipoPago, LocalDateTime fechaHoraInicial, LocalDateTime fechaHoraFinal) {
        BigDecimal total = ventaRepository.obtenerSumaTotalVenta(ventaStatus,tipoPago, fechaHoraInicial, fechaHoraFinal);
        return (total != null) ?  total : BigDecimal.valueOf(0.0);
    }

    @Override
    public BigDecimal obtenerGananciasEfectivo(LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        return ventaRepository.calcularGanancias(fechaInicial, fechaFinal);
    }

    @Override
    public Venta buscarVentaId(Integer id) {
        Optional<Venta> op = ventaRepository.findById(id);
        return op.orElse(null);
    }

    @Override
    public Venta obtenerUltimaVenta() {
        return ventaRepository.findFirstByOrderByFechaHoraVentaDesc();
    }

    @Override
    public LocalDateTime obtenerFechaHoraPrimeraVenta() {
        Venta venta =  ventaRepository.findFirstByOrderByIdVentaAsc();
        return venta.getFechaHoraVenta();
    }

    @Transactional
    @Override
    public void cancelarVenta(Venta venta, String usernameGerente, String passwordGerente) {
        // Validar credenciales del gerente
        if (!usuarioService.validarCredenciales(usernameGerente, passwordGerente, "GERENTE")) {
            throw new AccessDeniedException("Autorización denegada: Credenciales de gerente no válidas.");
        }

        //verificar que la venta existe
        Optional<Venta> ventaFind = ventaRepository.findById(venta.getIdVenta());

        if(ventaFind.isEmpty()){
            throw new IllegalArgumentException("La venta no existe");
        }

        // Verificar si la venta tiene productos vendidos asociados
        List<ProductoVendido> productosVendidos = productoVendidoService.obtenerProductosVendidosPorVenta(venta.getIdVenta());
        if (productosVendidos.isEmpty()) {
            throw new IllegalArgumentException("La venta no contiene productos. No es posible eliminar.");
        }

        if(productosVendidos != null){
            //si hay productos asociados cambiar su estatus a 'CANCELADO'
            productoVendidoService.cancelarProductosVendidosDeVenta(venta.getIdVenta());

            //Actualizar el stock de productos
           for (ProductoVendido productoVendido : productosVendidos) {
               // Actualizar stock del producto
               productoService.actualizarStockProducto(productoVendido.getProducto().getCodigo(), productoVendido.getCantidad(), "AUMENTAR");
           }
        }

        //cambiar el status de la venta a 'CANCELADA'
        venta.setVentaStatus("CANCELADA");
        ventaRepository.save(venta);
    }

    @Transactional
    @Override
    public ProductoVendido cancelarProducto(Venta venta, String codigo,  String usernameGerente, String passwordGerente) {
        // Validar credenciales del gerente
        if (!usuarioService.validarCredenciales(usernameGerente, passwordGerente, "GERENTE")) {
            throw new AccessDeniedException("Autorización denegada: Credenciales de gerente no válidas.");
        }

        //Validar que el producto este en la venta
        ProductoVendido productoACancelar = productoVendidoService.buscarProductoVendidoByCodigoAndIdVenta(venta.getIdVenta(), codigo);
        if (productoACancelar != null) {
            // Verificar si el producto ya está cancelado
            if ("CANCELADO".equalsIgnoreCase(productoACancelar.getPvStatus())) {
                throw new IllegalArgumentException("El producto ya está cancelado.");
            }

            //Cambiar el status a 'CANCELADO'
            productoVendidoService.cancelarProductoVendido(productoACancelar);
            //Actualizar el stock del producto
            productoService.actualizarStockProducto(codigo, productoACancelar.getCantidad(), "AUMENTAR");
            //Actualizar el total de la venta
            venta.setTotalVenta(venta.getTotalVenta().subtract(productoACancelar.getTotalProducto()));
            ventaRepository.save(venta);

            // Retornar el producto cancelado
            return productoACancelar;
        }else{
            throw new IllegalArgumentException("La venta no contiene ese producto.");
        }
    }

    @Override
    public List<ProductoVendido> getProductosVendidos(Integer idVenta) {
        return productoVendidoService.obtenerProductosVendidosPorVenta(idVenta);
    }

    @Override
    public List<Venta> obtenerVentasPorRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findVentasByFechaHoraVentaBetweenAndVentaStatus(fechaInicio, fechaFin, "FINALIZADA");
    }

    @Override
    public Page<Venta> obtenerVentasPorRangoYTipoPago(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago, Pageable pageable) {
        return ventaRepository.findVentasByFechaHoraVentaBetweenAndTipoPago(fechaInicio, fechaFin, tipoPago, pageable);
    }

    @Override
    public BigDecimal calcularSumaVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago) {
        return ventaRepository.obtenerSumaVentasPorTipoPago(fechaInicio, fechaFin, tipoPago);
    }

    @Override
    public BigDecimal calcularGananciasVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String tipoPago) {
        return ventaRepository.calcularGanancias(fechaInicio, fechaFin, tipoPago);
    }


}
