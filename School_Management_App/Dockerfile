FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the packaged application
COPY target/*.jar app.jar

# Set JVM options for memory constraints
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:-TieredCompilation"

# Expose port
EXPOSE 80

# Run the application with memory constraints
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar