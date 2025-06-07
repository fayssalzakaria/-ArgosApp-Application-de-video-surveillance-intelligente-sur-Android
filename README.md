# 📱 ArgosApp – Application de Vidéosurveillance Intelligente

Projet universitaire (L3 Informatique – Université Paris Cité) réalisé en équipe.  
ArgosApp est une application Android connectée à un backend Spring Boot, destinée à la vidéosurveillance en temps réel à partir de tablettes.

## 🔍 Fonctionnalités principales

- 🎥 Visionnage en **temps réel** de flux vidéo RTSP
- 🔔 Notifications **intelligentes** en temps réel via WebSockets
- 🔐 Authentification sécurisée via **JWT** et gestion des rôles (Admin / Utilisateur)
- 📁 Historique des enregistrements liés aux alertes
- 🧑‍💻 Interface optimisée pour **tablette Android**
- 🧠 Préparation à l'intégration d’un module **IA pour détection de vol**

## 🛠️ Stack technique

### Frontend (Android)
- Java (Android Studio)
- Retrofit (Appels API)
- WebRTC (Flux vidéo)
- Design responsive pour tablette

### Backend
- Java Spring Boot (REST API)
- PostgreSQL (Render)
- WebSocket + JWT (Sécurité & Communication)
- MediaMTX, FFmpeg, Ngrok (Gestion vidéo temps réel)

## 📦 Architecture

![Architecture](Image_archit.PNG)


## ⚙️ Lancement

Le backend est déjà déployé sur Render (aucune action nécessaire).

### Prérequis pour le frontend
- Android Studio
- Java 17+

### Lancer l'application Android
1. Ouvrir le dossier `ArgosFront` dans Android Studio
2. Modifier l’URL dans `ApiClient.java` si besoin (URL du backend Render)
3. Lancer sur un émulateur ou une tablette Android

## 📸 Captures d’écran

### 🔐 Écran de connexion
![Connexion](./im%20projet/Connection.PNG)

### 👥 Gestion des utilisateurs
![Utilisateurs](./im%20projet/Gestion_utilisateurs.PNG)

### 🎞️ Historique des alertes
![Historique](./im%20projet/Historique_Alerte.PNG)

### 📺 Visionnage en direct
![Surveillance](./im%20projet/Video_Surv.PNG)

### 📷 Gestion des caméras
![Gestion caméras](./im%20projet/Gest_cam.PNG)

### 👤 Espace compte utilisateur
![Compte utilisateur](./im%20projet/Gestion_compte.PNG)




## 👨‍💻 Équipe projet

- Mahdjouba Achab
- Melissa Zaid
- Rayan Boussad
- Fayssal Zakaria

Encadrants :  
Urim Maloku & Juventin Selvakumaran – *Argos Systems*

## 📅 Projet validé le 20/05/2025  
Licence 3 – Université Paris Cité

---

## 📜 Licence

Projet académique – Usage libre à but éducatif.
