# =========================
# 1️⃣ Build Stage
# =========================
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Copy Maven/Gradle build files first for caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
# If Gradle:
# COPY build.gradle gradlew ./
# COPY gradle gradle

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source and build the app
COPY src src
RUN ./mvnw clean package -DskipTests

# =========================
# 2️⃣ Runtime Stage
# =========================
FROM eclipse-temurin:21-jre AS runtime

# Set working directory
WORKDIR /app

# Copy only the built jar
COPY --from=builder /app/target/*.jar app.jar

# Set environment variables (optional but recommended)
ENV PORT=8080 \
    JAVA_OPTS="-Xms512m -Xmx1024m"

# Expose the same port Render will map
EXPOSE 8080

# Run the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
