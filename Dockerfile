# Use a JDK base image
FROM openjdk:17-jdk-slim as build

WORKDIR /app

# Copy Maven wrapper and set execute permissions
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy the rest of the source code
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the jar
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
