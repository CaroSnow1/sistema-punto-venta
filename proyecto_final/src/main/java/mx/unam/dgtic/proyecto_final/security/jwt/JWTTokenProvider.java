package mx.unam.dgtic.proyecto_final.security.jwt;

import mx.unam.dgtic.proyecto_final.auth.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTTokenProvider {

    private String secret;
    private int jwtExpirationInMs;
    private SecretKey key;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Genera el token JWT.
     *
     * @param authentication Información de autenticación.
     * @param usuario         Información del usuario autenticado.
     * @return Token JWT generado.
     */
    public String generateJwtToken(Authentication authentication, Usuario usuario) {
        Claims claims = Jwts.claims()
                .setSubject("proyecto_final") // Identificador de la aplicación.
                .setIssuer(usuario.getUsername()) // Usuario que generó el token.
                .setAudience("SPV Usuarios"); // Audiencia.

        claims.put("auth", authentication.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList()));
        claims.put("id", usuario.getIdUsuario()); // ID del usuario.
        claims.put("name", usuario.getNombre() + " " + usuario.getApPaterno() + " " + usuario.getApMaterno()); // Nombre completo.

        key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs * 1000L))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae los claims del token.
     *
     * @param token Token JWT.
     * @return Claims extraídos.
     */
    public Claims getClaims(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getFullName(String token) {
        return (String) getClaims(token).get("name");
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public String getIssuer(String token) {
        return getClaims(token).getIssuer();
    }

    public String getAudience(String token) {
        return getClaims(token).getAudience();
    }

    public Date getTokenExpiryFromJWT(String token) {
        return getClaims(token).getExpiration();
    }

    public Date getTokenIatFromJWT(String token) {
        return getClaims(token).getIssuedAt();
    }

    /**
     * Valida el token JWT.
     *
     * @param authToken Token JWT.
     * @return true si el token es válido, de lo contrario false.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .setAllowedClockSkewSeconds(300) // 5 minutos de margen
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido -> Mensaje: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado -> Mensaje: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado -> Mensaje: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims vacío -> Mensaje: {}", e.getMessage());
        }
        return false;
    }

    public long getExpiryDuration() {
        return jwtExpirationInMs * 1000L;
    }
}
