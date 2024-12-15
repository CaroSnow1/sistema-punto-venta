package mx.unam.dgtic.proyecto_final.security.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    private String token; // El token JWT
    private String tokenType = "Bearer"; // Tipo de token
    private Long userId; // ID del usuario
    private String userName; // Nombre del usuario
    private Long expiryDuration; // Duración de expiración del token
    private Collection<? extends GrantedAuthority> authorities; // Permisos del usuario

    public JwtRequest(String token, Long userId, String userName, Long expiryDuration,
                      Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.expiryDuration = expiryDuration;
        this.authorities = authorities;
        this.tokenType = "Bearer";
    }
}
