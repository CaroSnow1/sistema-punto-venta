package mx.unam.dgtic.proyecto_final.security.jwt;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.service.UsuarioService;
import mx.unam.dgtic.proyecto_final.security.dto.CredentialsDTO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mx.unam.dgtic.proyecto_final.security.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    public JWTAuthenticationFilter(JWTTokenProvider tokenProvider, UsuarioService usuarioService) {
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain) throws ServletException, IOException {

            //log.info("Contexto de seguridad antes del filtro: {}", SecurityContextHolder.getContext().getAuthentication());

            String jwt = extractTokenFromCookies(request);

            if (jwt == null || jwt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                if (tokenProvider.validateJwtToken(jwt)) {
                    Claims body = tokenProvider.getClaims(jwt);

                    var authorities = (List<Map<String, String>>) body.get("auth");
                    Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                            .map(auth -> new SimpleGrantedAuthority(auth.get("authority")))
                            .collect(Collectors.toSet());

                    String username = tokenProvider.getIssuer(jwt);

                    Usuario usuario = usuarioService.obtenerPorUsuario(username);
                    if (usuario == null) {
                        log.error("Usuario {} no encontrado en la base de datos", username);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autenticado");
                        return;
                    }

                    CredentialsDTO credentials = CredentialsDTO.builder()
                            .sub(tokenProvider.getSubject(jwt))
                            .aud(tokenProvider.getAudience(jwt))
                            .exp(tokenProvider.getTokenExpiryFromJWT(jwt).getTime())
                            .iat(tokenProvider.getTokenIatFromJWT(jwt).getTime())
                            .build();

                    /*var authentication = new UsernamePasswordAuthenticationToken(
                            username, credentials, simpleGrantedAuthorities);*/

                    var userDetails = new UserDetailsImpl(usuario); // Crear un objeto UserDetailsImpl basado en el Usuario
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, credentials, simpleGrantedAuthorities);


                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Autenticación establecida para: {}", username);
                }
            } catch (Exception exception) {
                log.error("No se pudo establecer la autenticación -> Mensaje: {}", exception.getMessage());
            }
            log.info("Contexto de seguridad después del filtro: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            filterChain.doFilter(request, response);
        }

        private String extractTokenFromCookies(HttpServletRequest request) {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
            return null;
        }
    }


