package mx.unam.dgtic.proyecto_final.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class CredentialsDTO {
    private String sub; // Nombre del usuario
    private String aud; // Audiencia (contexto del token)
    private Long iat; // Tiempo de emisión
    private Long exp; // Tiempo de expiración
}
