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
    
   // List<Logro> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT l FROM Logro l WHERE l.puntosRequeridos <= :puntosUsuario")
    List<Logro> findLogrosDisponibles(@Param("puntosUsuario") Integer puntosUsuario);
    
    @Query("SELECT l FROM Logro l JOIN UsuarioLogro ul ON l.id = ul.logro.id WHERE ul.usuario.id = :usuarioId")
    List<Logro> findLogrosByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT CASE WHEN COUNT(ul) > 0 THEN true ELSE false END " +
            "FROM UsuarioLogro ul WHERE ul.usuario.id = :usuarioId AND ul.logro.id = :logroId")
     boolean existsByUsuarioAndLogro(@Param("usuarioId") Long usuarioId, @Param("logroId") Long logroId);
}
