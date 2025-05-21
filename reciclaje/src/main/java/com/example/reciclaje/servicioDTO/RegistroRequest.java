package com.example.reciclaje.servicioDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroRequest {

	private String avatarId;

	@NotBlank(message = "La direccion es obligatorio")
	private String direccion;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "Debe ser un email válido")
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	private String password;

	@Pattern(regexp = "\\+?\\d{7,15}", message = "Número de teléfono inválido")
	private String telefono;

}
