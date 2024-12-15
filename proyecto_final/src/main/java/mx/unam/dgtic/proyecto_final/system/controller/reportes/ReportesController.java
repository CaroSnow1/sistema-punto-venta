package mx.unam.dgtic.proyecto_final.system.controller.reportes;

import lombok.extern.slf4j.Slf4j;
import mx.unam.dgtic.proyecto_final.system.model.dto.VentaDTO;
import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import mx.unam.dgtic.proyecto_final.system.service.categoria.CategoriaService;
import mx.unam.dgtic.proyecto_final.system.service.producto.ProductoService;
import mx.unam.dgtic.proyecto_final.system.service.venta.VentaService;
import mx.unam.dgtic.proyecto_final.system.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Controller
@RequestMapping(value = "reportes")
public class ReportesController {

    @Autowired
    VentaService ventaService;
    @Autowired
    ProductoService productoService;
    @Autowired
    CategoriaService categoriaService;


    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/ventas")
    public String mostrarReporteVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String tipoPago,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        log.info("Roles del usuario autenticado: {}",
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        // Formato de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Valores predeterminados
        LocalDateTime inicio = (fechaInicio != null && !fechaInicio.isEmpty()) ?
                LocalDateTime.parse(fechaInicio) :
                LocalDateTime.now().with(LocalTime.of(0, 1, 0));

        LocalDateTime fin = (fechaFin != null && !fechaFin.isEmpty()) ?
                LocalDateTime.parse(fechaFin) :
                LocalDateTime.now();

        String pago = (tipoPago != null && !tipoPago.isEmpty()) ? tipoPago : "E";

        Pageable pageable = PageRequest.of(page, size);

        // Obtener datos paginados
        Page<Venta> ventasPage = ventaService.obtenerVentasPorRangoYTipoPago(inicio, fin, pago, pageable);


        Page<VentaDTO> ventasDTOPage = ventasPage.map(venta -> new VentaDTO(
                venta.getFechaHoraVenta().format(formatter),
                venta.getTotalVenta(),
                venta.getTipoPago(),
                venta.getCajero() != null ? venta.getCajero().getNombreCompleto() : "Sin Cajero"
        ));

        BigDecimal sumaVentas = ventaService.calcularSumaVentas(inicio, fin, pago);
        BigDecimal gananciasVentas = ventaService.calcularGananciasVentas(inicio, fin, pago);

        // Crear el renderizador de páginas
        RenderPagina<VentaDTO> renderPagina = new RenderPagina<>("/reportes/ventas", ventasDTOPage);

        // Pasar datos al modelo
        model.addAttribute("ventas", ventasDTOPage.getContent());
        model.addAttribute("page", renderPagina);
        model.addAttribute("sumaVentas", sumaVentas);
        model.addAttribute("gananciasVentas", gananciasVentas);
        model.addAttribute("fechaInicio", inicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        model.addAttribute("fechaFin", fin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        model.addAttribute("tipoPago", pago);

        return "reportes/ventas";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/productos")
    public String mostrarReporteProductos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "categoria", required = false) Long categoriaId,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Producto> productoPage;

        // Filtrar por categoría o mostrar todos los productos con bajas/nulas existencias
        if (categoriaId != null) {
            productoPage = productoService.obtenerProductosConExistenciasBajasPorCategoria(categoriaId, pageable);
        } else {
            productoPage = productoService.obtenerProductosConExistenciasBajas(pageable);
        }


        // Obtener contadores
        // Obtener contadores
        long totalBajasExistencias = categoriaId != null
                ? productoService.contarProductosConExistenciasBajasPorCategoria(categoriaId)
                : productoService.contarProductosConExistenciasBajas();

        long totalAgotados = categoriaId != null
                ? productoService.contarProductosAgotadosPorCategoria(categoriaId)
                : productoService.contarProductosAgotados();

        // Pasar datos al modelo
        RenderPagina<Producto> renderPagina = new RenderPagina<>("/reportes/productos", productoPage);
        model.addAttribute("productos", productoPage.getContent());
        model.addAttribute("page", renderPagina);
        model.addAttribute("totalBajasExistencias", totalBajasExistencias);
        model.addAttribute("totalAgotados", totalAgotados);

        // Categorías para el filtro
        List<Categoria> categorias = categoriaService.buscarCategoria();
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriaSeleccionada", categoriaId);

        return "reportes/productos";
    }

}
