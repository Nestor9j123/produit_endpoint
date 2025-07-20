package com.dailycodebuffer.security.repositories;

import com.dailycodebuffer.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository pour gérer les opérations de base de données liées aux rôles
 * 
 * Cette interface étend JpaRepository qui fournit automatiquement les opérations
 * CRUD de base pour l'entité Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Recherche un rôle par son nom
     * 
     * @param name Le nom du rôle à rechercher
     * @return Le rôle trouvé ou null s'il n'existe pas
     */
    Optional<Role> findByName(String name);
    
    /**
     * Vérifie si un rôle existe par son nom
     * 
     * @param name Le nom du rôle à vérifier
     * @return true si le rôle existe, false sinon
     */
    boolean existsByName(String name);
    
    /**
     * Recherche tous les rôles actifs
     * 
     * @return Liste des rôles actifs
     */
    List<Role> findByActiveTrue();
    
    /**
     * Recherche des rôles par leurs noms
     * 
     * @param names Les noms des rôles à rechercher
     * @return Liste des rôles trouvés
     */
    List<Role> findByNameIn(Set<String> names);
    
    /**
     * Recherche des rôles par permission
     * 
     * @param permission La permission à rechercher
     * @return Liste des rôles ayant cette permission
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p = :permission")
    List<Role> findByPermission(@Param("permission") String permission);
    
    // SUPPRIMÉ : Recherche des rôles par utilisateur (relation users supprimée)
    // @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    // List<Role> findByUserId(@Param("userId") Long userId);
} 