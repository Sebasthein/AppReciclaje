package com.example.reciclaje.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

	private static final Logger logger = LoggerFactory.getLogger(LogService.class);

	// Registra validaciones de reciclaje
	public void registrarValidacion(Long usuarioId, Long reciclajeId, int puntos) {
		String mensaje = String.format("Reciclaje validado - ID: %d | Usuario: %d | Puntos otorgados: %d", reciclajeId,
				usuarioId, puntos);

		logger.info(mensaje);
		  // Opcional: Guardar en BD
       // logRepository.save(new LogAccion(mensaje, LocalDateTime.now()));

	}
}
