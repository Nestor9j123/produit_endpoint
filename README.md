<<<<<<< HEAD
# produit_endpoint
=======
# 🚀 Spring Boot Security JWT - Structure Optimisée

Une structure complète et optimisée pour l'authentification JWT avec Spring Boot Security, prête pour vos futurs projets.

## ✨ Fonctionnalités

### 🔐 **Sécurité Avancée**
- **JWT (JSON Web Tokens)** avec refresh tokens
- **BCrypt** pour le hachage des mots de passe
- **Rôles et permissions** granulaires
- **Verrouillage de compte** après échecs de connexion
- **Activation de compte** par email
- **Audit automatique** (création/modification)

### 🏗️ **Architecture Modulaire**
- **Séparation claire** des responsabilités
- **Injection de dépendances** Spring
- **Configuration externalisée**
- **Gestion d'exceptions** globale
- **Logs de sécurité** détaillés

### 📊 **Base de Données**
- **Entités optimisées** avec validation
- **Relations ManyToMany** pour les rôles
- **Audit automatique** JPA
- **Repositories** avec méthodes avancées

## 🛠️ Installation et Configuration

### 1. **Prérequis**
```bash
- Java 17+
- Maven 3.6+
- Base de données (H2, PostgreSQL, MySQL)
```

### 2. **Configuration**
Modifiez `application.properties` selon votre environnement :

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret=your-super-secret-jwt-key-256-bits-minimum
jwt.expiration=36000000
jwt.refresh-expiration=604800000
jwt.issuer=your-application-name
```

### 3. **Démarrage**
```bash
mvn spring-boot:run
```

## 📁 Structure du Projet

```
src/main/java/com/dailycodebuffer/security/
├── config/
│   ├── JwtAuthenticationFilter.java    # Filtre JWT
│   └── WebSecurityConfig.java          # Configuration sécurité
├── controller/
│   ├── UserController.java             # Endpoints utilisateurs
│   ├── ProductController.java          # Exemple endpoints protégés
│   └── WelcomeController.java          # Endpoint de bienvenue
├── entities/
│   ├── User.java                       # Entité utilisateur optimisée
│   └── Role.java                       # Entité rôle
├── repositories/
│   ├── UserRepository.java             # Repository utilisateur
│   └── RoleRepository.java             # Repository rôle
├── services/
│   ├── JwtService.java                 # Service JWT optimisé
│   ├── UserService.java                # Service utilisateur
│   ├── CustumUserDetailsService.java   # Service détails utilisateur
│   └── DataInitializationService.java  # Initialisation données
├── exception/
│   ├── GlobalExceptionHandler.java     # Gestionnaire exceptions
│   ├── JwtException.java               # Exception JWT
│   ├── ResourceNotFoundException.java  # Exception ressource non trouvée
│   └── ResourceConflictException.java  # Exception conflit ressource
└── CustumUserDetails.java              # Adaptateur UserDetails
```

## 🔄 Flux d'Authentification

### 1. **Inscription**
```http
POST /register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### 2. **Connexion**
```http
POST /login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Réponse :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 36000000
}
```

### 3. **Accès aux Ressources Protégées**
```http
GET /product
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 👥 Gestion des Rôles et Permissions

### **Rôles par Défaut**
- **ADMIN** : Accès complet au système
- **USER** : Utilisateur standard
- **MODERATOR** : Modérateur de contenu

### **Permissions Disponibles**
```java
// Gestion des utilisateurs
"USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE"

// Gestion des rôles
"ROLE_CREATE", "ROLE_READ", "ROLE_UPDATE", "ROLE_DELETE"

// Gestion des produits
"PRODUCT_CREATE", "PRODUCT_READ", "PRODUCT_UPDATE", "PRODUCT_DELETE"

// Système
"SYSTEM_MANAGE", "CONTENT_MODERATE"
```

## 🔧 Utilisation dans vos Projets

### 1. **Copier la Structure**
```bash
# Copiez les fichiers essentiels
cp -r src/main/java/com/dailycodebuffer/security/ your-project/src/main/java/
```

### 2. **Adapter les Entités**
```java
// Modifiez User.java selon vos besoins
@Entity
public class User {
    // Vos champs personnalisés
    private String phoneNumber;
    private String address;
    // ...
}
```

### 3. **Configurer les Endpoints**
```java
// Dans WebSecurityConfig.java
.authorizeHttpRequests(authorizeRequests -> authorizeRequests
    .requestMatchers("/api/public/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/user/**").hasRole("USER")
    .anyRequest().authenticated())
```

### 4. **Ajouter vos Services**
```java
@Service
public class YourBusinessService {
    // Votre logique métier
}
```

## 🚀 Fonctionnalités Avancées

### **Refresh Tokens**
```java
// Génération automatique de refresh tokens
String refreshToken = jwtService.generateRefreshToken(user);
```

### **Verrouillage de Compte**
```java
// Verrouillage automatique après 5 échecs
if (user.getFailedLoginAttempts() >= 5) {
    user.setStatus(AccountStatus.LOCKED);
}
```

### **Audit Automatique**
```java
// Dates automatiques
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime updatedAt;
```

### **Validation des Données**
```java
@NotBlank(message = "Le nom d'utilisateur est obligatoire")
@Size(min = 3, max = 50)
private String username;

@Email(message = "Format d'email invalide")
private String email;
```

## 📝 Exemples d'Utilisation

### **Créer un Utilisateur avec Rôles**
```java
@Autowired
private DataInitializationService dataService;

User newUser = dataService.createUserWithRoles(
    "john_doe",
    "john@example.com",
    "password123",
    "John",
    "Doe",
    Set.of("USER", "MODERATOR")
);
```

### **Vérifier les Permissions**
```java
@PreAuthorize("hasAuthority('USER_CREATE')")
public User createUser(User user) {
    // Logique de création
}
```

### **Gérer les Exceptions**
```java
// Exceptions automatiquement gérées par GlobalExceptionHandler
throw new ResourceNotFoundException("User", "id", userId);
throw new ResourceConflictException("User", "email", email);
```

## 🔒 Sécurité

### **Bonnes Pratiques Implémentées**
- ✅ **Mots de passe hachés** avec BCrypt
- ✅ **Tokens JWT sécurisés** avec signature HMAC
- ✅ **Validation des données** avec Bean Validation
- ✅ **Gestion des sessions** stateless
- ✅ **Logs de sécurité** détaillés
- ✅ **Verrouillage de compte** automatique
- ✅ **CORS configuré** pour les applications frontend

### **Configuration de Production**
```properties
# Clé JWT sécurisée (générer avec 256 bits minimum)
jwt.secret=${JWT_SECRET:your-production-secret-key}

# Expiration courte pour les tokens d'accès
jwt.expiration=900000

# Expiration longue pour les refresh tokens
jwt.refresh-expiration=604800000

# Désactiver les logs sensibles en production
logging.level.com.dailycodebuffer.security=INFO
```

## 🧪 Tests

### **Utilisateurs de Test Créés Automatiquement**
- **Admin** : `admin` / `admin123`
- **User** : `user` / `user123`

### **Endpoints de Test**
```http
# Test d'authentification
POST /login
{
  "username": "admin",
  "password": "admin123"
}

# Test d'accès protégé
GET /product
Authorization: Bearer <token>
```

## 📚 Documentation API

### **Endpoints Publics**
- `POST /register` - Inscription
- `POST /login` - Connexion

### **Endpoints Protégés**
- `GET /product` - Liste des produits
- `POST /product` - Créer un produit
- `GET /` - Page d'accueil

## 🤝 Contribution

1. Fork le projet
2. Créez une branche feature (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🆘 Support

Pour toute question ou problème :
- Ouvrez une issue sur GitHub
- Consultez la documentation Spring Security
- Vérifiez les logs de l'application

---

**🎉 Cette structure est prête pour vos futurs projets ! Adaptez-la selon vos besoins et commencez à développer !** 
>>>>>>> 4aaf4d5 (Initial commit du projet Spring Boot)
