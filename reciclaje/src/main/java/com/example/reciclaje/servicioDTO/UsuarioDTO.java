package com.example.reciclaje.servicioDTO;

import com.example.reciclaje.entidades.Usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {
	
	private Long id;
	private Long usuarioRolId;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String avatarId;
    private String direccion;
    private int puntos;
    private String nombreNivel; // Nombre del nivel
    private String rolAsignado;
 
    
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.puntos = usuario.getPuntos();
        this.nombreNivel = usuario.getNivel().getNombre();
       this.direccion= usuario.getDireccion();
      
    }

	public UsuarioDTO() {
		super();
	}
    
    
}
