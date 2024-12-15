package mx.unam.dgtic.proyecto_final.system.controller.categoria;

import jakarta.validation.Valid;
import mx.unam.dgtic.proyecto_final.system.converter.MayusculasConverter;
import mx.unam.dgtic.proyecto_final.system.model.entities.Categoria;
import mx.unam.dgtic.proyecto_final.system.service.categoria.CategoriaService;
import mx.unam.dgtic.proyecto_final.system.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "categoria")
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    @GetMapping("alta-categoria")
    public String altaCategoria(Model model) {
        Categoria categoria = new Categoria();
        categoria.setCatStatus("ACTIVO");
        model.addAttribute("contenido", "Agregar nueva Categoria");
        model.addAttribute("categoria", categoria);

        return "categoria/alta-categoria";
    }


    @PostMapping("salvar-categoria")
    public String salvarCategoria(@Valid @ModelAttribute("categoria") Categoria categoria, BindingResult result,
                             Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("contenido", "Error en el nombre, no debe de ser vacio");
            return "categoria/alta-categoria";
        }

        categoriaService.guardar(categoria);
        flash.addFlashAttribute("success", "Se almaceno con exito");

        return "redirect:/categoria/lista-categoria";
    }


    @GetMapping("lista-categoria")
    public String listaCategoria(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Categoria> categoriaEntities = categoriaService.buscarCategoria(pageable);
        RenderPagina<Categoria> renderPagina = new RenderPagina<>("lista-categoria", categoriaEntities);
        model.addAttribute("categoria", categoriaEntities);
        model.addAttribute("page", renderPagina);
        model.addAttribute("contenido", "Catalogo de Categorías");
        return "categoria/lista-categoria";
    }

    @GetMapping("eliminar-categoria/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes flash) {
        categoriaService.borrar(id);
        flash.addFlashAttribute("success", "Se borro con exito la categoria");
        return "redirect:/categoria/lista-categoria";
    }

    @GetMapping("modificar-categoria/{id}")
    public String saltoModificar(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = categoriaService.buscarCategoriaId(id);
        model.addAttribute("categoria", categoria);
        model.addAttribute("contenido", "Modificar Categoria");
        return "categoria/alta-categoria";

    }

    @InitBinder("categoria")
    public void nombreCategoria(WebDataBinder binder) {
        binder.registerCustomEditor(String.class,
                "nombreCategoria", new MayusculasConverter());
    }

}
