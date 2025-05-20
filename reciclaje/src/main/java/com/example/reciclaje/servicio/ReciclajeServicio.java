package com.example.reciclaje.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.EstadoReciclaje;
import com.example.reciclaje.entidades.Material;
import com.example.reciclaje.entidades.Nivel;
import com.example.reciclaje.entidades.Reciclaje;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.repositorio.MaterialRepositorio;
import com.example.reciclaje.repositorio.NivelRepositorio;
import com.example.reciclaje.repositorio.ReciclajeRepositorio;
import com.example.reciclaje.repositorio.UsuarioRepositorio;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReciclajeServicio {
	  private final ReciclajeRepositorio reciclajeRepository;
	    private final UsuarioServicio usuarioService;
	    private final MaterialRepositorio materialRepository;
	    private final NivelRepositorio nivelRepository;
	    private final UsuarioRepositorio usuarioRepository;
	    private final LogService logService;
	 

	    public Reciclaje registrarReciclaje(Long usuarioId, Long materialId, int cantidad) {
	        Usuario usuario = usuarioService.obtenerUsuario(usuarioId);
	        Material material = materialRepository.findById(materialId)
	            .orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));

	        Reciclaje reciclaje = new Reciclaje();
	        reciclaje.setUsuario(usuario);
	        reciclaje.setMaterial(material);
	        reciclaje.setCantidad(cantidad);
	        reciclajeRepository.save(reciclaje);

	        usuario.setPuntos(usuario.getPuntos() + reciclaje.getPuntosObtenidos());
	        actualizarNivelUsuario(usuario);

	        return reciclaje;
	    }

	    private void actualizarNivelUsuario(Usuario usuario) {
	        Nivel nuevoNivel = nivelRepository.findNivelByPuntos(usuario.getPuntos())
	            .orElseThrow(() -> new RuntimeException("Error al determinar nivel"));
	        
	        if (!nuevoNivel.equals(usuario.getNivel())) {
	            usuario.setNivel(nuevoNivel);
	        }
	    }
	    
	    public List<Reciclaje> obtenerReciclajesPorUsuario(Long usuarioId) {
	        return reciclajeRepository.findByUsuarioId(usuarioId);
	    }
	    
	    @Transactional
	    public Reciclaje validarReciclaje(Long id) {
	        // 1. Buscar el reciclaje por ID
	        Reciclaje reciclaje = reciclajeRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Reciclaje no encontrado con ID: " + id));

	        // 2. Validar que no esté ya validado
	        if (reciclaje.getValidado()) {
	            throw new IllegalStateException("El reciclaje ya fue validado anteriormente");
	        }

	        // 3. Obtener el usuario asociado
	        Usuario usuario = reciclaje.getUsuario();
	        
	        // 4. Calcular puntos (usando puntosPorUnidad del material)
	        int puntosGanados = reciclaje.getCantidad() * reciclaje.getMaterial().getPuntosPorUnidad();

	        // 5. Actualizar estado y puntos
	        reciclaje.setValidado(true);
	        reciclaje.setFechaValidacion(LocalDateTime.now());
	        
	        usuario.setPuntos(usuario.getPuntos() + puntosGanados);
	        usuarioService.actualizarNivel(usuario); // Actualiza nivel si corresponde

	        // 6. Guardar cambios
	        reciclaje = reciclajeRepository.save(reciclaje);
	        usuarioRepository.save(usuario);

	        // 7. Opcional: Registrar en historial/log
	        logService.registrarValidacion(usuario.getId(), reciclaje.getId(), puntosGanados);

	        return reciclaje;
	    }
}
