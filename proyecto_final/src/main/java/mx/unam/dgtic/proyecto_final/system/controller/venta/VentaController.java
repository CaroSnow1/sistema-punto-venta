package mx.unam.dgtic.proyecto_final.system.controller.venta;

import jdk.swing.interop.SwingInterOpUtils;
import lombok.extern.slf4j.Slf4j;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.security.model.UserDetailsImpl;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.model.entities.ProductoVendido;
import mx.unam.dgtic.proyecto_final.system.model.entities.Venta;
import mx.unam.dgtic.proyecto_final.system.service.producto.ProductoService;
import mx.unam.dgtic.proyecto_final.system.service.productoVendido.ProductoVendidoService;
import mx.unam.dgtic.proyecto_final.system.service.venta.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("venta")
public class VentaController {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private ProductoVendidoService productoVendidoService;
    @Autowired
    private VentaService ventaService;
    @Autowired
    private UsuarioService usuarioService;

    private List<ProductoVendido> productosVendidos = new ArrayList<>();
    private BigDecimal totalVenta = BigDecimal.ZERO;
    private Venta ventaActual;

    private Usuario obtenerCajeroEnSesion() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Principal desde SecurityContext: {}", principal);
        if (principal instanceof UserDetailsImpl) {
            String username = ((UserDetailsImpl) principal).getUsername();
            Usuario usuario = usuarioService.obtenerPorUsuario(username);
            log.info("Usuario obtenido de la base de datos: {}", usuario);
            return usuario;
        }
        log.warn("No se encontró un usuario autenticado en la sesión.");
        return null;
    }


    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("registrar-venta")
    public String altaVenta(Model model) {
        //Verificar si ya existe una venta creada no modificada ni finalizada
        Venta ventaAnterior = ventaService.obtenerUltimaVenta();

        System.out.println("Venta anterior: " + ventaAnterior.getIdVenta() + ventaAnterior.getVentaStatus());

        if(Objects.equals(ventaAnterior.getVentaStatus(), "CREADA")){
            //Si existe por lo tanto esa sea la venta actual
            ventaActual = ventaAnterior;
            productosVendidos.clear(); // Limpiar productos vendidos de cualquier venta anterior
            totalVenta = BigDecimal.ZERO;
            System.out.println("VENTA ANTERIOR CON STATUS CREADA");
        }else{
            //No existe, entonces verificamos si es una venta con la que estamos tarbajando actualmente
            if (ventaActual == null) {
                Usuario cajero = obtenerCajeroEnSesion();
                if (cajero == null) {
                    throw new IllegalStateException("No se encontró un usuario en sesión.");
                }
                ventaActual = ventaService.crearVentaDefault(cajero);

                productosVendidos.clear(); // Limpiar productos vendidos de cualquier venta anterior
                totalVenta = BigDecimal.ZERO;
                System.out.println("No hay venta con status CREADA");
                System.out.println("Se crea una venta: "+ ventaActual.getIdVenta() + ventaActual.getVentaStatus());
            }
        }

        model.addAttribute("venta", ventaActual);
        model.addAttribute("totalVenta", totalVenta);
        model.addAttribute("productosVendidos", productosVendidos);
        model.addAttribute("contenido", "Venta de productos");
        return "venta/registrar-venta";
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping("/agregarProducto")
    public String agregarProducto(@RequestParam String codigo, @RequestParam int cantidad, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.buscarProductoId(codigo);
            if (producto == null) throw new Exception("Producto no encontrado");

            // Agregar producto a la venta actual
            ventaService.agregarProductoAVenta(ventaActual, producto, cantidad);
            totalVenta = ventaService.obtenerTotalVenta(Long.valueOf(ventaActual.getIdVenta()));

            System.out.println("Venta Actual: " + ventaActual.getIdVenta() + ventaActual.getVentaStatus());

            ProductoVendido productoVendido = new ProductoVendido();
            productoVendido.setProducto(producto);
            productoVendido.setPrecioUnitario(producto.getPrecioVenta());
            productoVendido.setCantidad(cantidad);
            productoVendido.setTotalProducto(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
            productosVendidos.add(productoVendido);

            redirectAttributes.addFlashAttribute("mensajeExito", "Producto agregado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/venta/registrar-venta";
    }


    @PostMapping("/validar-gerente")
    @ResponseBody
    public Map<String, Boolean> validarGerente(@RequestBody Map<String, String> credenciales) {
        boolean autorizado = usuarioService.validarCredenciales(credenciales.get("username"), credenciales.get("password"), "GERENTE");
        return Map.of("autorizado", autorizado);
    }

    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/buscarProducto")
    @ResponseBody
    public Map<String, Object> buscarProducto(@RequestParam(required = false) String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código de producto no puede estar vacío");
        }

        // Buscar el producto vendido en la venta actual
        ProductoVendido productoVendido = productoVendidoService.buscarProductoVendidoByCodigoAndIdVenta(ventaActual.getIdVenta(), codigo);
        if (productoVendido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no está asociado a la venta actual");
        }

        // Preparar los detalles del producto vendido para la respuesta
        Map<String, Object> productoDetalles = new HashMap<>();
        productoDetalles.put("codigo", productoVendido.getProducto().getCodigo());
        productoDetalles.put("descripcion", productoVendido.getProducto().getNombre());
        productoDetalles.put("cantidad", productoVendido.getCantidad());
        return productoDetalles;
    }



    @PostMapping("/cancelar")
    public String cancelarVenta(@RequestParam String usernameGerente,
                                @RequestParam String passwordGerente,
                                RedirectAttributes redirectAttributes) {
        try {
            ventaService.cancelarVenta(ventaActual, usernameGerente, passwordGerente);
            ventaActual = null; // Resetear la venta actual
            productosVendidos.clear();
            totalVenta = BigDecimal.ZERO;
            redirectAttributes.addFlashAttribute("mensajeExito", "Venta cancelada exitosamente.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("error", "Autorización fallida: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la venta: " + e.getMessage());
        }
        return "redirect:/venta/registrar-venta";
    }

    @PostMapping("/cancelarProducto")
    public String cancelarProducto(@RequestParam String codigoProducto,
                                   @RequestParam String usernameGerente,
                                   @RequestParam String passwordGerente,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Lógica para cancelar el producto en el servicio
            ProductoVendido productoCancelado = ventaService.cancelarProducto(ventaActual, codigoProducto, usernameGerente, passwordGerente);

            // Actualizar la lista de productos vendidos
            productosVendidos.removeIf(producto ->
                    producto.getProducto().getCodigo().equalsIgnoreCase(codigoProducto)
            );
            // Actualizar el total de la venta
            if (productoCancelado != null) {
                totalVenta = totalVenta.subtract(productoCancelado.getTotalProducto());
            }
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto cancelado exitosamente.");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("error", "Autorización fallida: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar el producto: " + e.getMessage());
        }
        return "redirect:/venta/registrar-venta";
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping("/finalizar")
    public String finalizarVenta(@RequestParam(required = false) BigDecimal importePagado, RedirectAttributes redirectAttributes) {
        // Verificar que importePagado no sea nulo o negativo
        if (importePagado == null || importePagado.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar un importe pagado válido y mayor que cero para finalizar la venta.");
            return "redirect:/venta/registrar-venta";
        }

        try {
            // Finalizar la venta
            ventaService.finalizarVenta(ventaActual, importePagado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Venta finalizada exitosamente.");

            // Resetear datos de la venta después de finalizarla
            ventaActual = null;
            productosVendidos.clear();
            totalVenta = BigDecimal.ZERO;

            return "redirect:/venta/registrar-venta";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/venta/registrar-venta";
        }
    }

    /*@PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/checarPrecio")
    @ResponseBody
    public Map<String, Object> checarPrecio(@RequestParam String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código del producto no puede estar vacío.");
        }

        Producto producto = productoService.buscarProductoId(codigo);
        if (producto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto con código '" + codigo + "' no fue encontrado.");
        }

        Map<String, Object> productoDetalles = new HashMap<>();
        productoDetalles.put("codigo", producto.getCodigo());
        productoDetalles.put("descripcion", producto.getNombre());
        productoDetalles.put("precioVenta", producto.getPrecioVenta());
        productoDetalles.put("stock", producto.getStock());
        return productoDetalles;
    }*/

    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/checarPrecio")
    @ResponseBody
    public ResponseEntity<?> checarPrecio(@RequestParam String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "El código del producto no puede estar vacío."));
        }

        Producto producto = productoService.buscarProductoId(codigo);
        if (producto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El producto con código '" + codigo + "' no fue encontrado."));
        }

        Map<String, Object> productoDetalles = new HashMap<>();
        productoDetalles.put("codigo", producto.getCodigo());
        productoDetalles.put("descripcion", producto.getNombre());
        productoDetalles.put("precioVenta", producto.getPrecioVenta());
        productoDetalles.put("stock", producto.getStock());
        return ResponseEntity.ok(productoDetalles);
    }



}
