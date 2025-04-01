# Basis-Image mit Maven und JDK
FROM maven:3.8.6-eclipse-temurin-17 AS dependency-cache

# Arbeitsverzeichnis festlegen
WORKDIR /app

# Kopiere nur die pom.xml, um die Abhängigkeiten zu cachen
COPY weatherapp/pom.xml .

# Lade alle Maven-Abhängigkeiten herunter (ohne den Quellcode)
RUN mvn dependency:go-offline

# Finales Image: Maven + Abhängigkeiten, aber ohne den Quellcode
FROM maven:3.8.6-eclipse-temurin-17

WORKDIR /app

# Übernehme den Maven-Cache aus dem vorherigen Build-Stadium
COPY --from=dependency-cache /root/.m2 /root/.m2
COPY weatherapp/pom.xml .

EXPOSE 8080
# Standardkommando, das beim Starten des Containers ausgeführt wird.
# Hier wird der Spring Boot App-Start angestoßen.
CMD ["mvn", "spring-boot:run"]
