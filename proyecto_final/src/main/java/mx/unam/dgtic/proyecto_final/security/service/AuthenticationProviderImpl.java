package mx.unam.dgtic.proyecto_final.security.service;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import mx.unam.dgtic.proyecto_final.auth.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        log.info("Authentication: {}", authentication);
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado: " + username));

        // Validar si el usuario ya tiene una sesión activa
        if (usuario.getUsuStatus() == 1) {
            throw new BadCredentialsException("El usuario ya tiene una sesión activa.");
        }

        // Validar estado del usuario
        if (usuario.getUsuStatus() == 2) {
            throw new BadCredentialsException("El usuario está dado de baja.");
        }

        if (usuario.getUsuRol().equalsIgnoreCase("CAJERO")) {
            // Verificar si ya hay otro cajero con sesión activa
            boolean otroCajeroActivo = usuarioRepository.existsByCajeroActivoExcept(usuario.getIdUsuario());

            if (otroCajeroActivo) {
                throw new BadCredentialsException("Otro cajero ya tiene una sesión activa. Por favor, espera a que cierre sesión.");
            }
        }

        // Validar contraseña
        if (passwordEncoder.matches(password, usuario.getUsuPassword())) {
            log.info("Estado antes de guardar: {}", usuario.getUsuStatus());
            // Actualizar el estado a 1 (Activo)
            usuario.setUsuStatus(1);
            usuarioRepository.save(usuario);
            log.info("Estado después de guardar: {}", usuario.getUsuStatus());

            //Solo tienen un rol
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getUsuRol().toUpperCase());
            return new UsernamePasswordAuthenticationToken(username, password, List.of(authority));
        } else {
            throw new BadCredentialsException("Contraseña inválida");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
