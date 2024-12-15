package mx.unam.dgtic.proyecto_final.system.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cat_categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_categoria")
    private Integer idCategoria;

    @NotBlank(message = "El nombre de la categoría no debe estar en blanco")
    @Size(min = 3, max = 30, message = "El nombre de la categoría debe tener entre 3 y 30 caracteres")
    @Column(name="nombre_categoria")
    private String nombreCategoria;

    @NotBlank(message = "El estatus no puede ser nulo")
    @Pattern(regexp = "ACTIVO|INACTIVO", message = "El status debe ser 'ACTIVO' o 'INACTIVO'")
    @Column(name = "cat_status")
    private String catStatus;

    public Categoria(String nombreCategoria, String catStatus) {
        this.nombreCategoria = nombreCategoria;
        this.catStatus = catStatus;
    }

}
