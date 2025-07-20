package com.dailycodebuffer.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Arrays;

/**
 * Contrôleur REST d'exemple pour les produits.
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    @GetMapping
    public List<String> getProducts() {
        // Exemple statique
        return Arrays.asList("Produit 1", "Produit 2", "Produit 3");
    }
} 