# Use OpenJDK 17 runtime
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory
COPY target/*.jar app.jar

# Expose port 80
EXPOSE 80

# Run the application with dynamic profile (defaults to dev if not specified)
CMD java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} -jar app.jar
