package com.example.reciclaje.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import com.example.reciclaje.entidades.Logro;
import com.example.reciclaje.entidades.Usuario;
import com.example.reciclaje.servicio.LogroServicio;
import com.example.reciclaje.servicio.UsuarioServicio;
import com.example.reciclaje.servicioDTO.EstadisticasLogrosDTO;

@Controller
@RequestMapping("/api/")
public class LogroController {

    private static final Logger logger = LoggerFactory.getLogger(LogroController.class);

    private final LogroServicio logroService;
    private final UsuarioServicio usuarioService;

    @Autowired
    public LogroController(LogroServicio logroService, UsuarioServicio usuarioService) {
        this.logroService = logroService;
        this.usuarioService = usuarioService;
    }

 

    @GetMapping("/logros")
    public String mostrarLogros(Principal principal, Model model) {
        try {
            if (principal == null) {
                logger.warn("Intento de acceso no autenticado a /api/logros");
                return "redirect:/login";
            }

            String email = principal.getName();
            logger.debug("Buscando logros para usuario: {}", email);

            Usuario usuarioActual = usuarioService.findByEmail(email);
            if (usuarioActual == null) {
                logger.error("Usuario no encontrado en BD para email: {}", email);
                model.addAttribute("error", "Usuario no encontrado");
                return "error";
            }

            // Obtén todos los logros disponibles o los logros del usuario
            List<Logro> logros = logroService.obtenerLogrosPorUsuario(usuarioActual.getId());
            logger.info("Encontrados {} logros para usuario ID: {}", logros.size(), usuarioActual.getId());

            int puntosUsuario = usuarioActual.getPuntos();

            // Separar logros completados y en progreso
            List<Logro> logrosCompletados = logros.stream()
                .filter(logro -> logro.getPuntosRequeridos() <= puntosUsuario)
                .collect(Collectors.toList());

            List<Logro> logrosEnProgreso = logros.stream()
                .filter(logro -> logro.getPuntosRequeridos() > puntosUsuario)
                .collect(Collectors.toList());

            model.addAttribute("logrosCompletados", logrosCompletados);
            model.addAttribute("logrosEnProgreso", logrosEnProgreso);
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("puntosTotales", puntosUsuario);
            model.addAttribute("nivelActual", usuarioActual.getNivel().getNombre());

            // Si usas ranking, descomenta y adapta
            // model.addAttribute("ranking", usuarioService.obtenerRanking(usuarioActual.getId()));

            return "logros";

        } catch (Exception e) {
            logger.error("Error al obtener logros", e);
            model.addAttribute("error", "Ocurrió un error al cargar los logros");
            return "error";
        }
    }
}
