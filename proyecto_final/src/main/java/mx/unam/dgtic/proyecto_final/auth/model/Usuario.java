package mx.unam.dgtic.proyecto_final.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "El usuario no puede estar vacío")
    @Size(max = 20, message = "El usuario no puede tener más de 20 caracteres")
    private String username;

    //@NotBlank(message = "La contraseña no puede estar vacía")
    @Column(name = "usu_password")
    private String usuPassword;

    //@NotNull(message = "El estado del usuario es obligatorio")
    //@Min(value = 0, message = "Estado no válido")
    //@Max(value = 2, message = "Estado no válido")
    @Column(name = "usu_status")
    private Integer usuStatus;

    @NotBlank(message = "El rol del usuario es obligatorio")
    @Pattern(regexp = "GERENTE|CAJERO", message = "El rol debe ser 'GERENTE' o 'CAJERO'")
    @Column(name = "usu_rol")
    private String usuRol;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 35, message = "El nombre no puede tener más de 35 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido paterno no puede estar vacío")
    @Size(max = 35, message = "El apellido paterno no puede tener más de 35 caracteres")
    @Column(name = "ap_paterno")
    private String apPaterno;

    @Size(max = 35, message = "El apellido materno no puede tener más de 35 caracteres")
    @Column(name = "ap_materno")
    private String apMaterno;

    //@NotNull(message = "El horario de entrada es obligatorio")
    @Column(name = "horario_entrada")
    private Time horarioEntrada;

    //@NotNull(message = "El horario de salida es obligatorio")
    @Column(name = "horario_salida")
    private Time horarioSalida;

    @NotNull(message = "El género es obligatorio")
    @Pattern(regexp = "^[MFN]$", message = "El género debe ser 'M', 'F' o 'N'")
    private String genero;

    //@NotNull(message = "La fecha de alta es obligatoria")
    //@PastOrPresent(message = "La fecha de alta debe ser una fecha pasada o actual")
    @Column(name = "fecha_alta")
    private Date fechaAlta;

    @PastOrPresent(message = "La fecha de baja debe ser una fecha pasada o actual")
    @Column(name = "fecha_baja")
    private Date fechaBaja;

    // Relación Many-to-One con el creador (gerente)
    @ManyToOne
    @JoinColumn(name = "id_created_by", referencedColumnName = "id_usuario")
    private Usuario createdBy;

    public String getNombreCompleto() {
        return nombre + " " + apPaterno + " " + (apMaterno != null ? apMaterno : "");
    }

}
