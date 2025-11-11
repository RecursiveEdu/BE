# =========================
# 1️⃣ Build Stage
# =========================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper & configuration first
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Give executable permission to mvnw
RUN chmod +x mvnw

# Pre-download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy project source
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# =========================
# 2️⃣ Runtime Stage
# =========================
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Render’s expected port
ENV PORT=8080
EXPOSE 8080

# Run with Render’s dynamic port
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
