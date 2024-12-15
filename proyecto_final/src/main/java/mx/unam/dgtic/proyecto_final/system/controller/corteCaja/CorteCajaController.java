package mx.unam.dgtic.proyecto_final.system.controller.corteCaja;

import mx.unam.dgtic.proyecto_final.system.model.entities.CorteCaja;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.system.service.corteCaja.CorteCajaService;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.system.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping(value = "corte-caja")
public class CorteCajaController {

    @Autowired
    private CorteCajaService corteCajaService;

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/generar-corte")
    public String mostrarFormulario(Model model, Authentication authentication) {
        boolean esPrimerCorte = corteCajaService.esPrimerCorte();
        LocalDateTime fechaHoraActual = LocalDateTime.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        model.addAttribute("contenido", "Generar Corte Caja");
        model.addAttribute("esPrimerCorte", esPrimerCorte);
        model.addAttribute("fechaActual", fechaHoraActual.toLocalDate());
        model.addAttribute("horaActual", fechaHoraActual.toLocalTime().format(timeFormatter));
        model.addAttribute("saldoInicial", esPrimerCorte ? BigDecimal.ZERO : corteCajaService.calcularSaldoInicial());
        model.addAttribute("totalVendido", corteCajaService.calcularTotalVendido(fechaHoraActual));

        return "corte-caja/generar-corte";
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping("/guardar")
    public String guardarCorteCaja(@RequestParam BigDecimal importeEntregado,
                                   @RequestParam BigDecimal efectivoRemanente,
                                   @RequestParam String usuarioGerente,
                                   @RequestParam String passwordGerente,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime fechaHoraActual = LocalDateTime.now();

            // Obtener el usuario cajero en sesión
            Usuario cajero = usuarioService.obtenerPorUsuario(authentication.getName());

            // Validar las credenciales del gerente
            boolean credencialesValidas = usuarioService.validarCredenciales(usuarioGerente, passwordGerente, "GERENTE");

            if (!credencialesValidas) {
                throw new IllegalArgumentException("Credenciales del gerente no válidas.");
            }

            // Obtener el usuario gerente
            Usuario gerente = usuarioService.obtenerPorUsuario(usuarioGerente);

            // Preparar y guardar el corte de caja
            CorteCaja nuevoCorte = corteCajaService.prepararCorteCaja(fechaHoraActual, importeEntregado, efectivoRemanente);
            nuevoCorte.setCajero(cajero);
            nuevoCorte.setGerente(gerente);

            corteCajaService.guardar(nuevoCorte);

            redirectAttributes.addFlashAttribute("mensajeExito", "Corte de caja registrado con éxito.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el corte de caja: " + e.getMessage());
        }
        return "redirect:/corte-caja/generar-corte";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/consultar-corte")
    public String consultarCortesCaja(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Integer cajero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        List<Usuario> cajeros = usuarioService.obtenerUsuariosPorRol("CAJERO");
        model.addAttribute("cajeros", cajeros);

        LocalDateTime inicio = fechaInicio != null ? LocalDate.parse(fechaInicio).atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? LocalDate.parse(fechaFin).atTime(23, 59, 59) : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<CorteCaja> cortesDeCaja;

        if (inicio != null && fin != null && cajero != null) {
            cortesDeCaja = corteCajaService.obtenerCorteCajaByPeriodoAndIdCajero(inicio, fin, cajero, pageable);
        } else if (inicio != null && fin != null) {
            cortesDeCaja = corteCajaService.obtenerCorteCajaByPeriodo(inicio, fin, pageable);
        } else {
            cortesDeCaja = corteCajaService.buscarCorteCaja(pageable);
        }

        RenderPagina<CorteCaja> renderPagina = new RenderPagina<>("corte-caja/consultar-corte", cortesDeCaja);

        model.addAttribute("contenido", "Consultar Cortes de Caja");
        model.addAttribute("cortesDeCaja", cortesDeCaja.getContent());
        model.addAttribute("page", renderPagina);
        model.addAttribute("totalItems", cortesDeCaja.getTotalElements());

        return "corte-caja/consultar-corte";
    }
}
