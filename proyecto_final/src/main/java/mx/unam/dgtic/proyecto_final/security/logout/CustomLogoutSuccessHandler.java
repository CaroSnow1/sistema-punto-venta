package mx.unam.dgtic.proyecto_final.security.logout;

import mx.unam.dgtic.proyecto_final.auth.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.security.jwt.JWTTokenProvider;
import mx.unam.dgtic.proyecto_final.security.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JWTTokenProvider tokenProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Logout Handler iniciado");

        Usuario usuario = (authentication != null)
                ? obtenerUsuarioEnSesion()
                : obtenerUsuarioDesdeToken(request);

        if (usuario != null) {
            log.info("Usuario encontrado para cerrar sesión: {}", usuario.getUsername());
            usuario.setUsuStatus(0);
            usuarioRepository.save(usuario);
            log.info("Estado actualizado correctamente.");
        } else {
            log.warn("No se encontró un usuario autenticado en la sesión.");
        }

        // Manejo de cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    response.addCookie(cookie);
                    log.info("Cookie token invalidada correctamente");
                }
            }
        }

        response.sendRedirect(request.getContextPath());
    }

    private Usuario obtenerUsuarioEnSesion() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("No hay autenticación disponible en el SecurityContext.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.info("Principal desde SecurityContext: {}", principal);

        if (principal instanceof UserDetailsImpl) {
            String username = ((UserDetailsImpl) principal).getUsername();
            return usuarioRepository.findByUsername(username).orElse(null);
        }

        return null;
    }

    private Usuario obtenerUsuarioDesdeToken(HttpServletRequest request) {
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
            String username = tokenProvider.getIssuer(jwt);
            return usuarioRepository.findByUsername(username).orElse(null);
        }

        return null;
    }
}
