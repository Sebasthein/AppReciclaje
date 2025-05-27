package com.example.reciclaje.servicioDTO;

import lombok.Data;

@Data
public class EstadisticasLogrosDTO {
	private int logrosDesbloqueados;
    private int puntosObtenidos;
    private Integer proximoLogroCercano;
    
    public EstadisticasLogrosDTO(int logrosDesbloqueados, int puntosObtenidos, Integer proximoLogroCercano) {
        this.logrosDesbloqueados = logrosDesbloqueados;
        this.puntosObtenidos = puntosObtenidos;
        this.proximoLogroCercano = proximoLogroCercano;
    }

}
