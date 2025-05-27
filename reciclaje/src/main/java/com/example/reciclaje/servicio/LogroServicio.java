package com.example.reciclaje.servicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.Logro;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.repositorio.LogroRepositorio;
import com.example.reciclaje.repositorio.UsuarioRepositorio;

import jakarta.transaction.Transactional;

@Service
public class LogroServicio {
	
	 private final LogroRepositorio logroRepositorio;
	    private final UsuarioRepositorio usuarioRepositorio;

	    @Autowired
	    public LogroServicio(LogroRepositorio logroRepositorio, UsuarioRepositorio usuarioRepositorio) {
	        this.logroRepositorio = logroRepositorio;
	        this.usuarioRepositorio = usuarioRepositorio;
	    }

	    public List<Logro> obtenerTodosLogros() {
	        return logroRepositorio.findAll();
	    }

	    public Optional<Logro> obtenerLogroPorId(Long id) {
	        return logroRepositorio.findById(id);
	    }

	    public Logro crearLogro(Logro logro) {
	        return logroRepositorio.save(logro);
	    }

	    public Logro actualizarLogro(Long id, Logro logroActualizado) {
	        return logroRepositorio.findById(id)
	                .map(logro -> {
	                    logro.setNombre(logroActualizado.getNombre());
	                    logro.setDescripcion(logroActualizado.getDescripcion());
	                    logro.setImagenTrofeo(logroActualizado.getImagenTrofeo());
	                    return logroRepositorio.save(logro);
	                })
	                .orElseGet(() -> {
	                    logroActualizado.setId(id);
	                    return logroRepositorio.save(logroActualizado);
	                });
	    }

	    public void eliminarLogro(Long id) {
	        logroRepositorio.deleteById(id);
	    }

	    public List<Logro> obtenerLogrosPorUsuario(String email) {
	        Usuario usuario = usuarioRepositorio.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
	        return new ArrayList<>(usuario.getLogrosDesbloqueados());
	    }

	    public List<Logro> findByUsuarioId(Long id) {
	        return logroRepositorio.findByUsuarioId(id);
	    }
	    
	    /**
	     * Verifica y desbloquea logros para un usuario basado en su actividad de reciclaje
	     * @param usuario El usuario a verificar
	     */
	    @Transactional
	    public void verificarLogrosUsuario(Usuario usuario) {
	        // Obtener todos los logros disponibles
	        List<Logro> todosLogros = logroRepositorio.findAll();
	        
	        // Obtener logros ya desbloqueados por el usuario (convertimos a Set para mejor performance)
	        Set<Logro> logrosDesbloqueados = new HashSet<>(usuario.getLogrosDesbloqueados());
	        
	        // Verificar cada logro disponible
	        for (Logro logro : todosLogros) {
	            // Si el usuario no tiene este logro aún
	            if (!logrosDesbloqueados.contains(logro)) {
	                // Verificar si cumple los requisitos (puntos requeridos)
	                if (logro.getPuntosRequeridos() != null && 
	                    usuario.getPuntos() >= logro.getPuntosRequeridos()) {
	                    
	                    // Desbloquear el logro (bidireccional)
	                    usuario.getLogrosDesbloqueados().add(logro);
	                    logro.getUsuarios().add(usuario);
	                    
	                    // Guardar cambios
	                    usuarioRepositorio.save(usuario);
	                    logroRepositorio.save(logro);
	                    
	                    // Opcional: Aquí podrías registrar una notificación
	                }
	            }
	        }
	    }
	    
	    
	    
	   
}
