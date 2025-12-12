# Dockerfile
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers nécessaires
COPY pom.xml .
COPY src ./src

# Installer Maven et builder l'application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Copier le JAR généré
COPY target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
