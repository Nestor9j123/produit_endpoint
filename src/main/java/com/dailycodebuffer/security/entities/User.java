package com.dailycodebuffer.security.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité User optimisée pour les futurs projets
 *
 * Cette classe inclut :
 * - Validation des données avec Bean Validation
 * - Audit automatique (création/modification)
 * - Gestion des rôles avec relation ManyToMany
 * - Champs supplémentaires (email, statut, etc.)
 * - Support pour l'activation de compte
 *
 * @Data de Lombok génère automatiquement :
 * - Getters et setters pour tous les champs
 * - toString(), equals(), hashCode()
 * - Constructeurs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    /**
     * Identifiant unique de l'utilisateur
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Nom d'utilisateur unique
     * Validation : non vide, entre 3 et 50 caractères
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String username;

    /**
     * Adresse email unique
     * Validation : format email valide
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe haché
     * Validation : non vide, minimum 6 caractères
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(nullable = false)
    private String password;

    /**
     * Prénom de l'utilisateur
     */
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String firstName;

    /**
     * Nom de famille de l'utilisateur
     */
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String lastName;

    /**
     * Statut du compte (ACTIVE, INACTIVE, LOCKED, PENDING)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    /**
     * Indique si le compte est activé
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    /**
     * Date de la dernière connexion
     */
    private LocalDateTime lastLoginAt;

    /**
     * Nombre de tentatives de connexion échouées
     */
    @Column(nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    /**
     * Date de verrouillage du compte (si applicable)
     */
    private LocalDateTime lockedAt;

    /**
     * Token d'activation du compte
     */
    private String activationToken;

    /**
     * Date d'expiration du token d'activation
     */
    private LocalDateTime activationTokenExpiry;

    /**
     * Rôles de l'utilisateur (relation ManyToMany)
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Date de création (audit automatique)
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière modification (audit automatique)
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Méthodes utilitaires
     */

    /**
     * Ajoute un rôle à l'utilisateur
     */
    public void addRole(Role role) {
        this.roles.add(role);
        // NE PAS toucher à role.getUsers() ici pour éviter les problèmes de persistance
    }

    /**
     * Supprime un rôle de l'utilisateur
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        // NE PAS toucher à role.getUsers() ici pour éviter les problèmes de persistance
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * Vérifie si le compte est verrouillé
     */
    public boolean isAccountLocked() {
        return status == AccountStatus.LOCKED ||
                (lockedAt != null && lockedAt.plusMinutes(30).isAfter(LocalDateTime.now()));
    }

    /**
     * Incrémente le compteur de tentatives échouées
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.status = AccountStatus.LOCKED;
            this.lockedAt = LocalDateTime.now();
        }
    }

    /**
     * Réinitialise le compteur de tentatives échouées
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedAt = null;
        if (this.status == AccountStatus.LOCKED) {
            this.status = AccountStatus.ACTIVE;
        }
    }

    /**
     * Enum pour les statuts de compte
     */
    public enum AccountStatus {
        ACTIVE,     // Compte actif
        INACTIVE,   // Compte inactif
        LOCKED,     // Compte verrouillé
        PENDING     // En attente d'activation
    }
}
