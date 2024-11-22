# Gestionnaire SMS (Backend)

## Description
Application de gestion de SMS basée sur Kalkun, permettant la gestion des messages SMS via une interface web moderne. Ce backend est construit avec Spring Boot et Gradle et offre une API RESTful pour la gestion des messages, contacts et conversations.

## Fonctionnalités

- 📱 Gestion multi-utilisateurs et multi-modems
- 📬 Gestion des dossiers : boîte de réception, envoyés, spam, corbeille
- 💬 Vue conversation : regroupement des SMS par numéro de téléphone
- 👥 Gestion des contacts et des groupes
- ✉️ Options d'envoi de SMS avancées :
    - Envoi à un numéro, contact ou groupe
    - Programmation de messages
    - Réponse, transfert, renvoi
    - Fusion de SMS
    - Templates de messages
- 🔄 Signature automatique des messages
- 🚫 Possibilité de désactiver les SMS sortants

## Prérequis

- JDK 17 ou supérieur
- Gradle 8+
- PostgreSQL 13+
- IDE (recommandé : IntelliJ IDEA ou Eclipse)

## Installation

1. Cloner le dépôt :
```bash
git clone https://github.com/Everseed/sms-gateway.git
cd sms-gateway
```

2. Configurer la base de données PostgreSQL :
```bash
# Créer la base de données
createdb sms-gateway
```

3. Configurer les propriétés de l'application :
    - Copier `src/main/resources/application.properties.example` vers `src/main/resources/application.properties`
    - Modifier les paramètres de connexion à la base de données

4. Compiler et lancer l'application :
```bash
# Construire le projet
./gradlew build

# Nettoyer le build
./gradlew clean

# Lancer l'application
./gradlew bootRun

# Exécuter les tests
./gradlew test

# Générer le rapport de couverture
./gradlew jacocoTestReport

# Vérifier les dépendances obsolètes
./gradlew dependencyUpdates
```

## Structure du Projet

```
src/
├── main/
│   ├── java/
│   │   └── com/smsmanager/
│   │       ├── config/        # Configuration Spring et Sécurité
│   │       ├── controller/    # Contrôleurs REST
│   │       ├── dto/          # Objets de transfert de données
│   │       ├── exception/     # Gestion des exceptions
│   │       ├── model/        # Entités JPA
│   │       ├── repository/   # Repositories Spring Data
│   │       ├── service/      # Logique métier
│   │       └── util/         # Classes utilitaires
│   └── resources/
│       └── application.properties # Configuration de l'application
```

## Configuration

### Application Properties

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/sms_manager
spring.datasource.username=votre_username
spring.datasource.password=votre_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Sécurité
jwt.secret=votre_secret_jwt
jwt.expiration=86400000

# Configuration SMS Gateway
sms.gateway.url=http://localhost:8080
sms.gateway.api-key=votre_api_key
```

## API Endpoints

### Authentification
- POST `/api/auth/login` - Connexion utilisateur
- POST `/api/auth/register` - Inscription utilisateur

### Messages
- GET `/api/messages` - Liste des messages
- POST `/api/messages` - Envoi d'un message
- GET `/api/messages/{id}` - Détails d'un message
- DELETE `/api/messages/{id}` - Suppression d'un message

### Conversations
- GET `/api/conversations` - Liste des conversations
- GET `/api/conversations/{id}` - Détails d'une conversation
- GET `/api/conversations/{id}/messages` - Messages d'une conversation

### Contacts
- GET `/api/contacts` - Liste des contacts
- POST `/api/contacts` - Création d'un contact
- PUT `/api/contacts/{id}` - Modification d'un contact
- DELETE `/api/contacts/{id}` - Suppression d'un contact

## Sécurité

L'application utilise Spring Security avec JWT pour l'authentification. Chaque requête doit inclure un token JWT valide dans l'en-tête Authorization :

```
Authorization: Bearer <votre_token_jwt>
```

## Tests

Exécuter les tests unitaires :
```bash
./gradlew test
```

Exécuter les tests d'intégration :
```bash
./gradlew integrationTest
```

## Tâches Gradle utiles

Voici quelques tâches Gradle utiles pour le développement :

```bash
# Construire le projet
./gradlew build

# Nettoyer le build
./gradlew clean

# Lancer l'application
./gradlew bootRun

# Exécuter les tests
./gradlew test

# Générer le rapport de couverture
./gradlew jacocoTestReport

# Vérifier les dépendances obsolètes
./gradlew dependencyUpdates
```

## Contribuer

1. Fork le projet
2. Créer une branche (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -m 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.