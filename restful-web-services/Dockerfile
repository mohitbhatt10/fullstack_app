# Use an official OpenJDK 17 runtime as a base image
FROM khipu/openjdk17-alpine:latest

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/*.jar app.jar

# Copy the appropriate property file based on the environment
COPY src/main/resources/application-docker.properties /app/application.properties

# Expose the port that your application will run on
EXPOSE 8080

# Define the command to run your application
CMD ["java", "-jar", "app.jar"]