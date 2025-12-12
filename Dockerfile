# Étape 1 : Build avec Maven
FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
RUN git clone https://github.com/votre-utilisateur/votre-projet-java.git .
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
