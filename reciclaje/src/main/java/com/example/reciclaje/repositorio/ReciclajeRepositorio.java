package com.example.reciclaje.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reciclaje.entidades.Reciclaje;
import com.example.reciclaje.entidades.Usuario;

public interface ReciclajeRepositorio extends JpaRepository<Reciclaje, Long> {
	List<Reciclaje> findByUsuario(Usuario usuario); // Usado en servicio
    // List<Reciclaje> findByFecha(LocalDate fecha); // Eliminar si no se usa
    List<Reciclaje> findByUsuarioId(Long usuarioId); // Usado en servicio
    int countByUsuarioIdAndValidadoTrue(Long usuarioId); // Usado en servicio
    List<Reciclaje> findByValidadoFalse(); // Usado en servicio
    
    
}
