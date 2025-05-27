package com.example.reciclaje.entidades;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    @ManyToOne
    private Usuario usuario;

    

    // Solo una anotación @ManyToMany es necesaria
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
    
 
}
