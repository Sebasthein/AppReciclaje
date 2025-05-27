package com.example.reciclaje.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.reciclaje.entidades.Logro;

public interface LogroRepositorio extends JpaRepository<Logro, Long> {
	
	// Encuentra logros que requieran menos o igual puntos que los especificados
    List<Logro> findByPuntosRequeridosLessThanEqual(int puntos);
    
    // Encuentra el siguiente logro a desbloquear
    @Query("SELECT l FROM Logro l WHERE l.puntosRequeridos > :puntos ORDER BY l.puntosRequeridos ASC")
    List<Logro> findNextAchievements(@Param("puntos") int puntos);
    
    List<Logro> findByUsuarioId(Long usuarioId);
}
