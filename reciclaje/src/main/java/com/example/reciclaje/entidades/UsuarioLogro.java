package com.example.reciclaje.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_logro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime fechaObtencion;
    
    
    @ManyToOne(fetch = FetchType.LAZY) // Cambiar a LAZY
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY) // Cambiar a LAZY
    @JoinColumn(name = "logro_id")
    private Logro logro;

    // Constructor útil
    public UsuarioLogro(Usuario usuario, Logro logro, LocalDateTime fecha) {
        this.usuario = usuario;
        this.logro = logro;
        this.fechaObtencion = fecha;
    }
    
    // CORREGIR toString
    @Override
    public String toString() {
        return "UsuarioLogro{" +
               "id=" + id +
               ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
               ", logroId=" + (logro != null ? logro.getId() : null) +
               '}';
    }

	public UsuarioLogro(Usuario usuario2, Logro logro2) {
		this.usuario = usuario;
        this.logro = logro;
	}
  

}