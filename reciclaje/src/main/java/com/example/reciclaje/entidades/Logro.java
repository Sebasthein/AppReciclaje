package com.example.reciclaje.entidades;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    
    

    @OneToMany(mappedBy = "logro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioLogro> usuarioLogros = new HashSet<>();

    // CORREGIR hashCode (solo incluir ID)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    
    
 
}
