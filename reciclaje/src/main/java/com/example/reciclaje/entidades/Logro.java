package com.example.reciclaje.entidades;

import java.util.HashSet;
import java.util.Set;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;

=======
>>>>>>> parent of 5d9ad46 (Revert "Merge branch 'main' of https://github.com/Sebasthein/AppReciclaje")
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "logros")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String imagenTrofeo; 
    @Column(nullable = true) // Temporalmente nullable
    private Integer puntosRequeridos; // Cambiado de int a Integer
    
<<<<<<< HEAD
    @ToString.Exclude
    @JsonIgnore
=======
>>>>>>> parent of 5d9ad46 (Revert "Merge branch 'main' of https://github.com/Sebasthein/AppReciclaje")
    @ManyToOne
    private Usuario usuario;

    

    // Solo una anotación @ManyToMany es necesaria
<<<<<<< HEAD
    @ToString.Exclude
    @JsonIgnore
=======
>>>>>>> parent of 5d9ad46 (Revert "Merge branch 'main' of https://github.com/Sebasthein/AppReciclaje")
    @ManyToMany(mappedBy = "logrosDesbloqueados", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();
    
    // En la clase Logro
    public void agregarUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        usuario.getLogrosDesbloqueados().add(this);
    }

    public void eliminarUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        usuario.getLogrosDesbloqueados().remove(this);
    }
    
<<<<<<< HEAD
    @Override
    public String toString() {
        return "Logro{" +
            "id=" + id +
            ", nombre='" + nombre + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", imagenTrofeo='" + imagenTrofeo + '\'' +
            ", puntosRequeridos=" + puntosRequeridos +
            '}';
    }
    
=======
>>>>>>> parent of 5d9ad46 (Revert "Merge branch 'main' of https://github.com/Sebasthein/AppReciclaje")
 
}
