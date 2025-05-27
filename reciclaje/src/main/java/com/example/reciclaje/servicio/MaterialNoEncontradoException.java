package com.example.reciclaje.servicio;

public class MaterialNoEncontradoException extends RuntimeException{

	public MaterialNoEncontradoException(String message) {
        super(message);
    }
}
