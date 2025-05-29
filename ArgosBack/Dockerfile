# 1. Utiliser une image officielle avec OpenJDK 17
FROM openjdk:17-jdk-slim

# 2. Définir JAVA_HOME et le PATH pour Java
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH="$JAVA_HOME/bin:$PATH"

# 4. Définir le répertoire de travail
WORKDIR /app

# 5. Copier le contenu de l'application
COPY argosapp/ /app

# 6. Donner les permissions et exécuter le build sans tests
RUN chmod +x ./gradlew && ./gradlew build -x test

# 7. Exécuter les tests après le build
RUN chmod +x ./gradlew && ./gradlew test --continue


# 8. Exposer le port de l'application
EXPOSE 8080

# 9. Lancer l'application
CMD ["java", "-jar", "build/libs/argosapp-0.0.1-SNAPSHOT.jar"]
