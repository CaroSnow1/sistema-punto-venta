package mx.unam.dgtic.proyecto_final.system.controller.usuario;

import jakarta.validation.Valid;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.system.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;
import java.util.Date;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("gestionar-usuario")
    public String gestionarUsuario(@RequestParam(name = "rol", required = false) String rol,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Usuario> usuarios;

        if (rol != null && !rol.isEmpty()) {
            usuarios = usuarioService.buscarUsuarioPorRol(rol, pageable);
        } else {
            usuarios = usuarioService.buscarUsuario(pageable);
        }

        long totalUsuarios = usuarios.getTotalElements();
        RenderPagina<Usuario> renderPagina = new RenderPagina<>("gestionar-usuario", usuarios);

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("contenido", "Gestión de Usuarios");
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("page", renderPagina);

        return "usuario/gestionar-usuario";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("alta-usuario")
    public String altaUsuario(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "Alta de Usuario");
        return "usuario/agregar-usuario";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping("alta-usuario")
    public String altaUsuario(@RequestParam("username") String username,
                              @RequestParam("usuPassword") String usuPassword,
                              @RequestParam("nombre") String nombre,
                              @RequestParam("apPaterno") String apPaterno,
                              @RequestParam("apMaterno") String apMaterno,
                              @RequestParam("usuRol") String usuRol,
                              @RequestParam("horarioEntrada") String horarioEntrada,
                              @RequestParam("horarioSalida") String horarioSalida,
                              @RequestParam("genero") String genero,
                              RedirectAttributes flash) {
        try {
            Usuario usuario = new Usuario();

            // Mapear valores del formulario al objeto Usuario
            usuario.setUsername(username);
            usuario.setUsuPassword(passwordEncoder.encode(usuPassword)); // Codificar contraseña
            usuario.setNombre(nombre);
            usuario.setApPaterno(apPaterno);
            usuario.setApMaterno(apMaterno);
            usuario.setUsuRol(usuRol);
            usuario.setHorarioEntrada(Time.valueOf(horarioEntrada + ":00")); // Convertir String a Time
            usuario.setHorarioSalida(Time.valueOf(horarioSalida + ":00")); // Convertir String a Time
            usuario.setGenero(genero);
            usuario.setFechaAlta(new Date()); // Fecha de alta como fecha actual
            usuario.setUsuStatus(0); // Estado inicial: Inactivo

            // Guardar el usuario en la base de datos
            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success", "Usuario creado con éxito");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", "Formato de hora no válido. Use el formato HH:mm.");
            return "usuario/agregar-usuario";
        }

        return "redirect:/usuario/gestionar-usuario";
    }


    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("editar-usuario/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.buscarUsuarioId(id);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/gestionar-usuario";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "Editar Usuario");
        return "usuario/editar-usuario";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping("modificar-usuario")
    public String modificarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult result,
                                   RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "usuario/editar-usuario";
        }

        // Buscar el usuario existente en la base de datos
        Usuario usuarioExistente = usuarioService.buscarUsuarioId(usuario.getIdUsuario());
        if (usuarioExistente == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/gestionar-usuario";
        }

        // Mantener valores no editables
        usuario.setUsuPassword(usuarioExistente.getUsuPassword()); // Mantener la contraseña existente
        usuario.setFechaAlta(usuarioExistente.getFechaAlta()); // Mantener la fecha de alta
        usuario.setFechaBaja(usuarioExistente.getFechaBaja()); // Mantener la fecha de baja si existe
        usuario.setCreatedBy(usuarioExistente.getCreatedBy()); // Mantener el creador
        usuario.setUsuStatus(usuarioExistente.getUsuStatus()); // Mantener el estado actual

        // Guardar los cambios
        usuarioService.guardar(usuario);
        flash.addFlashAttribute("success", "Usuario actualizado con éxito");
        return "redirect:/usuario/gestionar-usuario";
    }


    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("baja-usuario/{id}")
    public String bajaUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.buscarUsuarioId(id);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/gestionar-usuario";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "Dar de Baja Usuario");
        return "usuario/baja-usuario";
    }

    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping("confirmar-baja-usuario")
    public String confirmarBajaUsuario(@RequestParam Integer idUsuario, RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarUsuarioId(idUsuario);
        if (usuario == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/gestionar-usuario";
        }

        usuario.setUsuStatus(2); // Marcar como baja
        usuario.setFechaBaja(new Date());
        usuarioService.guardar(usuario);

        flash.addFlashAttribute("success", "Usuario dado de baja con éxito");
        return "redirect:/usuario/gestionar-usuario";
    }
}
