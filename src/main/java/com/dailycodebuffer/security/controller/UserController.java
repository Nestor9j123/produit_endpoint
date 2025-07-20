package com.dailycodebuffer.security.controller;

import com.dailycodebuffer.security.entities.User;
import com.dailycodebuffer.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des utilisateurs et l'authentification.
 */
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Endpoint pour l'inscription d'un nouvel utilisateur
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User created = userService.register(user);
        return ResponseEntity.ok(created);
    }

    /**
     * Endpoint pour la connexion d'un utilisateur
     * POST /auth/login
     * Retourne un JWT si la connexion réussit
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        String jwt = userService.verify(user);
        if (jwt == null || jwt.equals("fail")) {
            return ResponseEntity.status(401).body("Identifiants invalides");
        }
        return ResponseEntity.ok(jwt);
    }

    /**
     * Endpoint pour obtenir la liste de tous les utilisateurs (protégé)
     * GET /auth/users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Endpoint pour obtenir un utilisateur par son id (protégé)
     * GET /auth/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 