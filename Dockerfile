# Basis-Image mit JDK 17 auf Alpine
FROM eclipse-temurin:17-jdk-alpine

# Arbeitsverzeichnis im Container setzen
WORKDIR /app

# Wichtige Dateien kopieren
COPY weatherapp/.mvn .mvn
COPY weatherapp/mvnw .
COPY weatherapp/pom.xml .
COPY weatherapp/src src

# Maven Wrapper ausführbar machen
RUN chmod +x mvnw

# Abhängigkeiten herunterladen (um Docker-Layer-Caching zu optimieren)
RUN ./mvnw dependency:go-offline

# Anwendung bauen
RUN ./mvnw package -DskipTests


# Port für die Spring Boot Anwendung freigeben
EXPOSE 8080

# Healthcheck für den Container
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Anwendung starten
CMD ["./mvnw", "spring-boot:run"]
