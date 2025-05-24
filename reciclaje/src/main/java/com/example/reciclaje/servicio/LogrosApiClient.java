package com.example.reciclaje.servicio;

import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.Logro;

@Service
public class LogrosApiClient {
	
	  public Logro obtenerLogroPorClave(String claveLogro) {
	        // Aquí iría la llamada real a una API externa o lógica simulada
	        Logro logro = new Logro();
	        logro.setNombre(claveLogro);
	        logro.setDescripcion("Logro obtenido: " + claveLogro);
	        return logro;
	    }

}
