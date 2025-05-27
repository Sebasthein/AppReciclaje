package com.example.reciclaje.servicioDTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LogroDTO {
	private String nombre;
    private String descripcion;
    private String imagenTrofeo; // si aún la usas para algo adicional
    private String icono; // Ejemplo: "fa-recycle"
    private String rareza; // "bronze", "silver", "gold", "platinum"
    private boolean desbloqueado;
    private int progresoActual;
    private int objetivo;
    private int porcentajeCompletado;
    private LocalDate fechaDesbloqueo;
    private String categoria; // "recycling", "community", "streaks", "special"


}
