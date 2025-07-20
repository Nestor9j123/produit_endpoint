package com.dailycodebuffer.security.entities;

import jakarta.persistence.*;
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
 * Entité Role pour gérer les rôles des utilisateurs
 * 
 * Cette classe permet de définir des rôles avec des permissions.
 * La relation ManyToMany est gérée uniquement côté User.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {
    
    /**
     * Identifiant unique du rôle
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    /**
     * Nom du rôle (unique)
     */
    @Column(unique = true, nullable = false, length = 50)
    @EqualsAndHashCode.Include
    private String name;
    
    /**
     * Description du rôle
     */
    @Column(length = 255)
    private String description;
    
    /**
     * Indique si le rôle est actif
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    /**
     * Permissions associées au rôle
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission", length = 100)
    @Builder.Default
    private Set<String> permissions = new HashSet<>();
    
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
     * Ajoute une permission au rôle
     */
    public void addPermission(String permission) {
        this.permissions.add(permission);
    }
    
    /**
     * Supprime une permission du rôle
     */
    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }
    
    /**
     * Vérifie si le rôle a une permission spécifique
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Vérifie si le rôle a au moins une des permissions données
     */
    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vérifie si le rôle a toutes les permissions données
     */
    public boolean hasAllPermissions(String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
} 