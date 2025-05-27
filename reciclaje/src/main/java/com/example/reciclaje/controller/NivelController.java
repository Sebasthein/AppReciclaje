package com.example.reciclaje.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.reciclaje.entidades.Nivel;
import com.example.reciclaje.servicio.NivelServicio;
import com.example.reciclaje.servicioDTO.NivelDTO;

@Controller
public class NivelController {
	
	@Autowired
	private  NivelServicio nivelServicio;
	
	
	@GetMapping("/niveles")
	public List<NivelDTO> obtenerNiveles() {
	    List<Nivel> niveles = nivelServicio.obtenerTodos();
	    return niveles.stream().map(NivelDTO::new).collect(Collectors.toList());
	}

}
