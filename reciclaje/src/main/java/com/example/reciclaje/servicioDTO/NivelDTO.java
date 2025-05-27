package com.example.reciclaje.servicioDTO;

import com.example.reciclaje.entidades.Nivel;

import lombok.Data;

@Data
public class NivelDTO {
	
	   private Long id;
	    private String nombre;
	    private int puntosRequeridos;

	    // Constructor con Nivel
	    public NivelDTO(Nivel nivel) {
	        this.id = nivel.getId();
	        this.nombre = nivel.getNombre();
	        this.puntosRequeridos = nivel.getPuntosRequeridos();
	    }

}
