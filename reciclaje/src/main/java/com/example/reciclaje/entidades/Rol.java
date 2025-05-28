package com.example.reciclaje.entidades;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String nombre;

	    private String descripcion;

	    @ManyToMany(mappedBy = "roles")
	    private Set<Usuario> usuarios = new HashSet<>();
	    
	    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true)
	    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

	    // Constructor adicional
	    public Rol(String nombre, String descripcion) {
	        this.nombre = nombre;
	        this.descripcion = descripcion;
	    }
}
