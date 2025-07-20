package com.dailycodebuffer.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour la page de bienvenue.
 */
@RestController
public class WelcomeController {
    @GetMapping("/")
    public String welcome() {
        return "Bienvenue sur l'API sécurisée !";
    }
} 