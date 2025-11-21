# API REST Java - Architecture Hexagonale avec JWT

API REST en Spring Boot avec architecture hexagonale, sécurité JWT et validation d'adresse via API externe.

## Architecture

```
src/main/java/com/letocart/java_apirest_2026/
├── entity/          # Entités JPA
│   ├── User.java
│   ├── Book.java
│   └── Category.java
├── repository/      # Repositories
│   ├── UserRepository.java
│   ├── BookRepository.java
│   └── CategoryRepository.java
├── service/         # Services
│   ├── UserService.java
│   ├── BookService.java
│   ├── CategoryService.java
│   └── GeocodingService.java
├── controller/      # Controllers REST
│   ├── AuthController.java (Login/Register)
│   ├── UserController.java
│   ├── BookController.java
│   └── CategoryController.java
├── security/        # Sécurité JWT
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
├── dto/             # DTO
│   ├── CreateUserRequest.java
│   ├── CreateBookRequest.java
│   └── CreateCategoryRequest.java
└── exception/       # Gestion des erreurs
    └── GlobalExceptionHandler.java
```

## Mise en place du projet

### 1. Démarrer MySQL avec Docker Compose

```bash
docker-compose up -d
```

Ou manuellement :
```bash
docker run --name my_mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=mydb \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  -d mysql:8.1
```

### 2. Initialiser la base de données

```bash
docker exec -i my_mysql mysql -uuser -ppassword mydb < shema.sql
```

### 3. Compiler et démarrer l'application

```bash
./mvnw clean spring-boot:run
```

L'API sera disponible sur : `http://localhost:8080`

## Liste des Endpoints API

### Authentification (Public)

#### S'enregistrer
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secret123",
  "email": "john@example.com",
  "address": "1 rue de la Paix, 75001 Paris"
}
```

**Validation automatique de l'adresse** : L'API vérifie l'existence de l'adresse via l'API IGN Géocodage avant de créer l'utilisateur.

Exemple avec curl :
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Greg",
    "password": "gregou",
    "email": "gregory.febvin@etu.unilasalle.fr",
    "address": "10 rue de Rivoli, Paris"
  }'
```

#### Se connecter
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "secret123"
}
```

Réponse :
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

Exemple avec curl :
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "greg.feb20@gmail.com",
    "password": "gregou"
  }'
```

---

### Users (Protégé - JWT requis)

**Note** : Pour les routes protégées, ajoutez le header :
```
Authorization: Bearer <votre_token_jwt>
```

#### Lister tous les utilisateurs
```bash
GET http://localhost:8080/api/users
```

Exemple avec curl :
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Récupérer un utilisateur par ID
```bash
GET http://localhost:8080/api/users/1
```

Exemple avec curl :
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### Categories (Protégé - JWT requis)

#### Créer une catégorie
```bash
POST http://localhost:8080/api/categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Science-Fiction"
}
```

Exemple avec curl :
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Fantasy"}'
```

#### Lister toutes les catégories
```bash
GET http://localhost:8080/api/categories
Authorization: Bearer <token>
```

Exemple avec curl :
```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer <token>"
```

#### Récupérer une catégorie par ID
```bash
GET http://localhost:8080/api/categories/1
Authorization: Bearer <token>
```

---

### Books (Protégé - JWT requis)

#### Créer un livre
```bash
POST http://localhost:8080/api/books
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Dune",
  "categoryIds": [1, 2]
}
```

Exemple avec curl :
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Le Seigneur des Anneaux", "categoryIds": [1]}'
```

#### Lister tous les livres
```bash
GET http://localhost:8080/api/books
Authorization: Bearer <token>
```

Exemple avec curl :
```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer <token>"
```

#### Récupérer un livre par ID
```bash
GET http://localhost:8080/api/books/1
Authorization: Bearer <token>
```

---

### Tests individuels simples

```bash
# Créer un utilisateur
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Bob",
    "password": "bobpass",
    "email": "bob@test.com",
    "address": "5 Avenue des Champs-Élysées, Paris"
  }'

# Se connecter
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob@test.com",
    "password": "bobpass"
  }'

# Créer une catégorie (remplacer <TOKEN> par le token JWT)
curl -i -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Science-Fiction"}'

# Lister les catégories
curl -i -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer <TOKEN>"

# Créer un livre
curl -i -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Dune", "categoryIds": [1]}'

# Lister les livres
curl -i -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer <TOKEN>"
```

## Structure de la base de données

Les tables sont créées automatiquement par le script `shema.sql` :

### Table `user`
```sql
CREATE TABLE `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(512) NOT NULL,
    roles VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    refresh_token VARCHAR(512) DEFAULT NULL,
    last_password_reset TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);
```

### Table `category`
```sql
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
```

### Table `book`
```sql
CREATE TABLE book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);
```

### Table `book_category` (relation many-to-many)
```sql
CREATE TABLE book_category (
    book_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);
```

## Validations

Les DTOs incluent des validations Jakarta Bean Validation :
- **username** : 3-50 caractères, requis, unique
- **password** : minimum 6 caractères, requis (hashé avec BCrypt)
- **email** : format email valide, requis, unique
- **address** : requis, validé via API IGN Géocodage
- **title** : requis (pour les livres)
- **name** : requis (pour les catégories)

Toute violation renvoie un code 400 avec les détails.

## Validation d'adresse

Lors de l'inscription d'un utilisateur, l'adresse est validée automatiquement via l'API IGN Géocodage :
- URL utilisée : `https://api-gouv.lab.rioc.fr/search?q={adresse}`
- Si l'adresse n'existe pas ou n'est pas trouvée, l'inscription échoue

## Sécurité JWT

### Fonctionnement
1. **Inscription** : `/api/auth/register` - Crée un compte utilisateur (public)
2. **Connexion** : `/api/auth/login` - Retourne un token JWT (public)
3. **Routes protégées** : Toutes les autres routes nécessitent le header `Authorization: Bearer <token>`

### Configuration JWT
- **Secret** : Clé secrète pour signer les tokens (configurable dans `application.properties`)
- **Expiration** : 1 heure par défaut (3600000 ms)
- **Algorithme** : HMAC SHA-256

### Exemple de scénario complet
```bash
# 1. S'inscrire
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123","email":"user1@test.com","address":"10 rue de Rivoli, Paris"}'

# 2. Se connecter et récupérer le token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@test.com","password":"pass123"}'
# Réponse: {"accessToken":"eyJ...","tokenType":"Bearer"}

# 3. Utiliser le token pour accéder aux ressources protégées
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJ..."
```

## Gestion d'erreurs

- `400 Bad Request` : Validation échouée (champs manquants/invalides)
- `401 Unauthorized` : Token JWT manquant, invalide ou expiré
- `403 Forbidden` : Accès refusé
- `404 Not Found` : Ressource introuvable
- `409 Conflict` : Username ou email déjà existant
- Messages d'erreur clairs en JSON

Exemple de réponse d'erreur :
```json
{
  "error": "Username already exists"
}
```

### Test de l'erreur 401

Tentative d'accéder à une route protégée sans token JWT :

```bash
curl -i -X GET http://localhost:8080/api/users
```

Réponse attendue :
```
HTTP/1.1 401 
Content-Type: application/json
...

{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required. Please provide a valid JWT token.",
  "path": "/api/users"
}
```

Avec un token invalide ou expiré :
```bash
curl -i -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer invalid_token_here"
```

Réponse : même erreur 401 avec message JSON.

## Docker Compose

Le fichier `docker-compose.yaml` configure MySQL :

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.1
    container_name: my_mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mydb
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql

volumes:
  db_data:
```
