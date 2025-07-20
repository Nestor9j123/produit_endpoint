package com.dailycodebuffer.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration JPA pour l'application
 * 
 * Cette classe active :
 * - L'audit automatique JPA (@CreatedDate, @LastModifiedDate)
 * - Les repositories JPA
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.dailycodebuffer.security.repositories")
public class JpaConfig {
    // Configuration automatique par Spring Boot
} 