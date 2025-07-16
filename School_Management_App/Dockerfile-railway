# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /build

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Stage 2: Run the application with a slim JDK image
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the packaged application
COPY --from=build /build/target/*.jar app.jar

# Set JVM options for memory constraints
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:-TieredCompilation"

# Expose port
EXPOSE 80

# Run the application with memory constraints
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar