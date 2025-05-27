package com.example.reciclaje.entidades;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reciclajes")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reciclaje {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@JsonIgnore
	 @ManyToOne
	    @JoinColumn(name = "usuario_id", nullable = false)
	    private Usuario usuario;

	    @ManyToOne
	    @JoinColumn(name = "material_id", nullable = false)
	    private Material material;

	    private int cantidad;
	    private LocalDateTime fecha;
	    private int puntosObtenidos; // = cantidad * material.puntosPorUnidad
	    @Column(nullable = false)
	    private boolean validado = false;
	    private LocalDateTime fechaValidacion;

		

	    @PrePersist
	    public void calcularPuntos() {
	        this.puntosObtenidos = this.cantidad * material.getPuntosPorUnidad();
	        this.fecha = LocalDateTime.now();
	    }

	    public boolean getValidado() {
	        return this.validado;
	    }

	    public void setValidado(boolean validado) {
	        this.validado = validado;
	        if(validado) {
	            this.fechaValidacion = LocalDateTime.now();
	        }
	    }
}
