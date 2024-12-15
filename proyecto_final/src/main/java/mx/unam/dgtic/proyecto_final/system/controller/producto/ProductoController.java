package mx.unam.dgtic.proyecto_final.system.controller.producto;

import jakarta.validation.Valid;
import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import mx.unam.dgtic.proyecto_final.system.model.entities.Producto;
import mx.unam.dgtic.proyecto_final.system.service.categoria.CategoriaService;
import mx.unam.dgtic.proyecto_final.system.service.producto.ProductoService;
import mx.unam.dgtic.proyecto_final.system.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "producto")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @Autowired
    CategoriaService categoriaService;

    @GetMapping("alta-producto")
    public String altaProducto(Model model) {
        Producto producto = new Producto();
        List<Categoria> selectCategoria = categoriaService.buscarCategoria();
        model.addAttribute("contenido", "Agregar un nuevo Producto");
        model.addAttribute("producto", producto);
        model.addAttribute("selectCategoria", selectCategoria);

        return "producto/alta-producto";
    }


    @PostMapping("salvar-producto")
    public String salvarProducto(@Valid @ModelAttribute("producto")Producto producto, BindingResult result,
                                  Model model, RedirectAttributes flash) {
        List<Categoria> selectCategoria = categoriaService.buscarCategoria();
        model.addAttribute("selectCategoria", selectCategoria);

        if (result.hasErrors()) {
            model.addAttribute("contenido", "Error en el nombre, no debe de ser vacio");
            return "producto/alta-producto";
        }
        //Asignar Status Activo
        if (producto.getProdStatus() == null) {
            producto.setProdStatus("ACTIVO");
        }
        productoService.guardar(producto);
        flash.addFlashAttribute("success", "Se almacenó con éxito!");
        return "redirect:/producto/lista-producto";
    }


    @GetMapping("lista-producto")
    public String listaProducto(@RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "categoria", required = false) Long categoriaId,
                                Model model) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Producto> productoEntities;

        if (categoriaId != null) {
            productoEntities = productoService.buscarProductoPorCategoria(categoriaId, pageable); // Método para filtrar por categoría
        } else {
            productoEntities = productoService.buscarProducto(pageable); // Método para obtener todos los productos
        }

        RenderPagina<Producto> renderPagina = new RenderPagina<>("lista-producto", productoEntities);

        long totalProductos = productoService.contarProductos();
        if (categoriaId != null) {
            totalProductos = productoService.contarProductosPorCategoria(categoriaId);
        } else {
            totalProductos = productoService.contarProductos();
        }


        model.addAttribute("producto", productoEntities);
        model.addAttribute("page", renderPagina);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("contenido", "Catálogo de Productos");

        // Cargar las categorías para el select
        List<Categoria> categorias = categoriaService.buscarCategoria(); // Método para obtener todas las categorías
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriaSeleccionada", categoriaId);

        return "producto/lista-producto";
    }


    @GetMapping("eliminar-producto/{id}")
    public String eliminar(@PathVariable("id") String id, RedirectAttributes flash) {
        productoService.darDeBaja(id);
        flash.addFlashAttribute("success", "El producto se dió de baja con éxito!");
        return "redirect:/producto/lista-producto";
    }

    @GetMapping("modificar-producto/{id}")
    public String saltoModificar(@PathVariable("id") String id, Model model) {
        Producto producto = productoService.buscarProductoId(id);
        List<Categoria> selectCategoria = categoriaService.buscarCategoria();
        model.addAttribute("producto", producto);
        model.addAttribute("selectCategoria", selectCategoria);
        model.addAttribute("contenido", "Modificar Producto");
        return "producto/alta-producto";

    }

    /*@InitBinder("producto")
    public void nombreProducto(WebDataBinder binder) {
        binder.registerCustomEditor(String.class,
                "nombre", new MayusculasConverter());
    }*/

}
