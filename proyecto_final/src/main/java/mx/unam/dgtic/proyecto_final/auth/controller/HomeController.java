package mx.unam.dgtic.proyecto_final.auth.controller;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mx.unam.dgtic.proyecto_final.security.jwt.JWTTokenProvider;
import mx.unam.dgtic.proyecto_final.security.request.JwtRequest;
import mx.unam.dgtic.proyecto_final.security.request.LoginUserRequest;

@Slf4j
@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    public HomeController(UsuarioService usuarioService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/inicio")
    public String inicioPublico() {
        return "inicio"; // Vista pública para todos los usuarios
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/inicio";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Vista protegida para usuarios autenticados
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Página de login
    }

    @PostMapping("/token")
    public String createAuthenticationToken(Model model, HttpSession session,
                                            @ModelAttribute LoginUserRequest loginUserRequest, HttpServletResponse response) throws Exception {
        log.info("LoginUserRequest: {}", loginUserRequest);
        System.out.println("Se llamo al token");
        try {
            Usuario usuario = usuarioService.obtenerPorUsuario(loginUserRequest.getUsername());
            if (usuario == null) {
                throw new BadCredentialsException("Usuario no encontrado");
            }

            if (usuario.getUsuStatus() == 2) { // Usuario dado de baja
                session.setAttribute("msg", "Usuario dado de baja");
                return "redirect:/login";
            }

            // Validar si otro cajero está activo
            if (usuario.getUsuRol().equalsIgnoreCase("CAJERO") && usuarioService.existeOtroCajeroActivo(usuario.getIdUsuario())) {
                session.setAttribute("msg", "Otro cajero ya tiene una sesión activa.");
                return "redirect:/login";
            }

            Authentication authentication = authenticate(loginUserRequest.getUsername(),
                    loginUserRequest.getPassword());

            // Actualizar el estado del usuario a 1 (Activo)
            usuario.setUsuStatus(1);
            usuarioService.guardar(usuario);

            String jwtToken = jwtTokenProvider.generateJwtToken(authentication, usuario);
            log.info("JWT Token: {}", jwtToken);

            JwtRequest jwtRequest = new JwtRequest(jwtToken, usuario.getIdUsuario().longValue(), usuario.getUsername(),
                    jwtTokenProvider.getExpiryDuration(), authentication.getAuthorities());

            Cookie cookie = new Cookie("token", jwtToken);
            cookie.setPath("/"); // Hacerla accesible para todas las rutas
            cookie.setMaxAge((int) jwtTokenProvider.getExpiryDuration() / 1000);
            response.addCookie(cookie);

            log.info("Autenticación exitosa para el usuario: {}", usuario.getUsername());
            log.info("Redirigiendo a /dashboard");
            session.setAttribute("msg", "Login exitoso");
        } catch (BadCredentialsException e) {
            log.error("Error de credenciales: {}", e.getMessage());
            session.setAttribute("msg", "Credenciales inválidas");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error en el proceso de autenticación: {}", e.getMessage());
            session.setAttribute("msg", "Error en el inicio de sesión");
            return "redirect:/login";
        }


        return "redirect:/dashboard";
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("Usuario deshabilitado", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales inválidas", e);
        }
    }
}
