# Spécifications de l'Application de Gestion SMS

## 1. Description Générale
Application de gestion de SMS basée sur Kalkun, permettant la gestion des messages SMS via une interface web moderne. Elle utilise gammu-smsd comme moteur de passerelle SMS pour délivrer et récupérer les messages.

## 2. Fonctionnalités Principales

### 2.1 Gestion des Utilisateurs
- **Authentification**
  - Inscription avec email/mot de passe
  - Connexion sécurisée avec JWT
  - Gestion des rôles (USER, ADMIN, MODERATOR)
  - Déconnexion avec invalidation des tokens

- **Profil Utilisateur**
  - Modification des informations personnelles
  - Changement de mot de passe
  - Gestion des préférences

### 2.2 Gestion des Messages
- **Envoi de Messages**
  - Envoi simple à un numéro
  - Envoi groupé
  - Programmation de messages
  - Support des modèles

- **Réception et Stockage**
  - Réception en temps réel
  - Stockage organisé
  - Historique complet
  - Statuts des messages (PENDING, SENT, DELIVERED, FAILED)

- **Organisation**
  - Dossiers (inbox, outbox, sent, spam, trash)
  - Conversations groupées par contact
  - Recherche avancée
  - Filtres personnalisables

### 2.3 Gestion des Contacts
- **Contacts**
  - Ajout/modification/suppression
  - Import/export
  - Organisation en groupes
  - Historique des conversations

- **Groupes**
  - Création de groupes
  - Gestion des membres
  - Envoi groupé
  - Statistiques

### 2.4 Modèles de Messages
- **Templates**
  - Création de modèles
  - Variables personnalisables
  - Catégorisation
  - Réutilisation

- **Variables**
  - Types supportés (TEXT, NUMBER, DATE, BOOLEAN)
  - Valeurs par défaut
  - Validation
  - Personnalisation

### 2.5 Fonctionnalités Avancées
- **Programmation**
  - Messages planifiés
  - Récurrence
  - Annulation/modification
  - Notifications

- **Statistiques et Rapports**
  - Usage par utilisateur
  - Taux de délivrance
  - Analyse des coûts
  - Rapports exportables

## 3. Architecture Technique

### 3.1 Backend
- **Framework**: Spring Boot
- **Base de données**: PostgreSQL
- **Cache**: Redis
- **Sécurité**: Spring Security + JWT
- **Documentation**: OpenAPI/Swagger

### 3.2 Services Principaux
- **UserService**: Gestion des utilisateurs
- **MessageService**: Gestion des messages
- **TemplateService**: Gestion des modèles
- **ContactService**: Gestion des contacts
- **SMSGatewayService**: Interface avec Gammu

### 3.3 Structure de la Base de Données
- **Users**: Gestion des utilisateurs
- **Messages**: Stockage des messages
- **Contacts**: Carnet d'adresses
- **Templates**: Modèles de messages
- **Groups**: Groupes de contacts
- **Conversations**: Organisation des messages

## 4. Sécurité

### 4.1 Authentification
- JWT pour l'authentification
- Refresh tokens
- Blacklist des tokens invalides
- Protection contre les attaques courantes

### 4.2 Autorisation
- RBAC (Role-Based Access Control)
- Permissions granulaires
- Isolation des données par utilisateur
- Validation des entrées

## 5. API REST

### 5.1 Endpoints Principaux
- `/api/auth`: Authentification
- `/api/messages`: Gestion des messages
- `/api/templates`: Gestion des modèles
- `/api/contacts`: Gestion des contacts
- `/api/admin`: Administration

### 5.2 Formats de Données
```json
// Message
{
    "id": "long",
    "content": "string",
    "phoneNumber": "string",
    "status": "enum(PENDING,SENT,DELIVERED,FAILED)",
    "type": "enum(SMS,TEMPLATE)",
    "scheduledAt": "datetime",
    "createdAt": "datetime"
}

// Template
{
    "id": "long",
    "name": "string",
    "content": "string",
    "variables": [
        {
            "key": "string",
            "type": "enum(TEXT,NUMBER,DATE,BOOLEAN)",
            "defaultValue": "string"
        }
    ]
}
```

## 6. Monitoring et Maintenance

### 6.1 Logging
- Logs applicatifs
- Logs de sécurité
- Logs de performance
- Rotation des logs

### 6.2 Métriques
- Temps de réponse
- Taux de succès/échec
- Utilisation des ressources
- Alertes

