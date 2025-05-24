package com.example.reciclaje.servicio;

import org.springframework.stereotype.Service;

import com.example.reciclaje.entidades.Logro;
import com.example.reciclaje.entidades.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogroServicio {

    private final LogrosApiClient logrosApiClient;

    public void asignarLogroSiNoTiene(Usuario usuario, String claveLogro) {
        boolean yaTiene = usuario.getLogros().stream()
            .anyMatch(l -> l.getNombre().equalsIgnoreCase(claveLogro));

        if (!yaTiene) {
            Logro logro = logrosApiClient.obtenerLogroPorClave(claveLogro);
            usuario.getLogros().add(logro);
        }
    }
}
