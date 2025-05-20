package com.example.reciclaje.entidades;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioRol {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "usuario_id", nullable = false)
	    private Usuario usuario;

	    @ManyToOne
	    @JoinColumn(name = "rol_id", nullable = false)
	    private Rol rol;

	    private boolean activo = true;
	    private LocalDate fechaAsignacion = LocalDate.now();

	    // Constructor para fácil creación
	    public UsuarioRol(Usuario usuario, Rol rol) {
	        this.usuario = usuario;
	        this.rol = rol;
	    }
	

}
