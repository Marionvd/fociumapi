# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

# Copy Maven wrapper first so dependency layer is cached separately from source
COPY .mvn .mvn
COPY mvnw .
# Fix Windows CRLF line endings so the script runs on Linux
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:26-jdk
WORKDIR /app

COPY --from=build /app/target/FociumAPI-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
