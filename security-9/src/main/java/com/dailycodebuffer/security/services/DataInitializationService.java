package com.dailycodebuffer.security.services;

import com.dailycodebuffer.security.entities.Role;
import com.dailycodebuffer.security.entities.User;
import com.dailycodebuffer.security.repositories.RoleRepository;
import com.dailycodebuffer.security.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service d'initialisation des données pour les futurs projets
 * 
 * Ce service s'exécute au démarrage de l'application pour :
 * - Créer les rôles par défaut
 * - Créer un utilisateur administrateur
 * - Initialiser les permissions de base
 * 
 * Implémente CommandLineRunner pour s'exécuter automatiquement au démarrage
 */
@Service
public class DataInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public DataInitializationService(RoleRepository roleRepository, 
                                   UserRepository userRepository, 
                                   BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Démarrage de l'initialisation des données...");
        
        // Création des rôles par défaut
        createDefaultRoles();
        
        // Force le flush et clear du contexte de persistance
        entityManager.flush();
        entityManager.clear();
        
        // Création de l'utilisateur administrateur
        createAdminUser();
        
        // Création d'un utilisateur de test
        createTestUser();
        
        logger.info("Initialisation des données terminée avec succès!");
    }
    
    /**
     * Crée les rôles par défaut du système
     */
    private void createDefaultRoles() {
        logger.info("Création des rôles par défaut...");
        
        // Rôle ADMIN
        Role adminRole = createRoleIfNotExists("ADMIN", "Administrateur du système", Arrays.asList(
            "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
            "ROLE_CREATE", "ROLE_READ", "ROLE_UPDATE", "ROLE_DELETE",
            "PRODUCT_CREATE", "PRODUCT_READ", "PRODUCT_UPDATE", "PRODUCT_DELETE",
            "SYSTEM_MANAGE"
        ));
        
        // Rôle USER
        Role userRole = createRoleIfNotExists("USER", "Utilisateur standard", Arrays.asList(
            "PRODUCT_READ",
            "PROFILE_READ", "PROFILE_UPDATE"
        ));
        
        // Rôle MODERATOR
        Role moderatorRole = createRoleIfNotExists("MODERATOR", "Modérateur", Arrays.asList(
            "USER_READ", "USER_UPDATE",
            "PRODUCT_CREATE", "PRODUCT_READ", "PRODUCT_UPDATE",
            "CONTENT_MODERATE"
        ));
        
        logger.info("Rôles créés: ADMIN, USER, MODERATOR");
    }
    
    /**
     * Crée un rôle s'il n'existe pas déjà
     * 
     * @param name Nom du rôle
     * @param description Description du rôle
     * @param permissions Permissions du rôle
     * @return Le rôle créé ou existant
     */
    private Role createRoleIfNotExists(String name, String description, java.util.List<String> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(name)
                            .description(description)
                            .active(true)
                            .permissions(new HashSet<>(permissions))
                            .build();
                    
                    Role savedRole = roleRepository.save(role);
                    logger.info("Rôle créé: {}", name);
                    return savedRole;
                });
    }
    
    /**
     * Crée l'utilisateur administrateur par défaut
     */
    private void createAdminUser() {
        String adminUsername = "admin";
        
        if (userRepository.existsByUsername(adminUsername)) {
            logger.info("L'utilisateur administrateur existe déjà");
            return;
        }
        
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
        // Recharge le rôle pour qu'il soit bien initialisé (pas un proxy)
        adminRole = roleRepository.findById(adminRole.getId()).orElseThrow();
        
        User adminUser = User.builder()
                .username(adminUsername)
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("System")
                .status(User.AccountStatus.ACTIVE)
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();
        
        userRepository.save(adminUser);
        logger.info("Utilisateur administrateur créé: {} / {}", adminUsername, "admin123");
    }
    
    /**
     * Crée un utilisateur de test
     */
    private void createTestUser() {
        String testUsername = "user";
        
        if (userRepository.existsByUsername(testUsername)) {
            logger.info("L'utilisateur de test existe déjà");
            return;
        }
        
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rôle USER non trouvé"));
        // Recharge le rôle pour qu'il soit bien initialisé (pas un proxy)
        userRole = roleRepository.findById(userRole.getId()).orElseThrow();
        
        User testUser = User.builder()
                .username(testUsername)
                .email("user@example.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("Test")
                .lastName("User")
                .status(User.AccountStatus.ACTIVE)
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
        
        userRepository.save(testUser);
        logger.info("Utilisateur de test créé: {} / {}", testUsername, "user123");
    }
    
    /**
     * Méthode utilitaire pour créer des permissions personnalisées
     * 
     * @param roleName Nom du rôle
     * @param permissions Nouvelles permissions à ajouter
     */
    public void addPermissionsToRole(String roleName, Set<String> permissions) {
        roleRepository.findByName(roleName).ifPresent(role -> {
            role.getPermissions().addAll(permissions);
            roleRepository.save(role);
            logger.info("Permissions ajoutées au rôle {}: {}", roleName, permissions);
        });
    }
    
    /**
     * Méthode utilitaire pour créer un nouvel utilisateur avec rôles
     * 
     * @param username Nom d'utilisateur
     * @param email Email
     * @param password Mot de passe (sera encodé)
     * @param firstName Prénom
     * @param lastName Nom
     * @param roleNames Noms des rôles à assigner
     * @return L'utilisateur créé
     */
    public User createUserWithRoles(String username, String email, String password, 
                                  String firstName, String lastName, Set<String> roleNames) {
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("L'utilisateur " + username + " existe déjà");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("L'email " + email + " est déjà utilisé");
        }
        
        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(roleNames));
        
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .status(User.AccountStatus.ACTIVE)
                .enabled(true)
                .roles(roles)
                .build();
        
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé: {} avec rôles: {}", username, roleNames);
        return savedUser;
    }
} 