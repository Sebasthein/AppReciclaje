package com.example.reciclaje.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.Nivel;
import com.example.reciclaje.repositorio.NivelRepositorio;

@Service
public class NivelServicio {

	 @Autowired
	    private NivelRepositorio nivelRepository;
	
	public Nivel obtenerNivelPorPuntos(int puntos) {
	    return nivelRepository.findTopByPuntosMinimosLessThanEqualOrderByPuntosMinimosDesc(puntos)
	                          .orElseThrow(() -> new RuntimeException("No se encontró nivel para los puntos"));
	}

}
