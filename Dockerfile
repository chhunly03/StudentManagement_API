# BUILD STAGE
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and necessary build files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Ensure Gradle wrapper has execute permission
RUN chmod +x ./gradlew

# Clean and build the application
RUN ./gradlew clean build -x test

# RUN STAGE (Use Java 21 JRE)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose the application port
EXPOSE 8181

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
