FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the packaged application
COPY target/*.jar app.jar

# Set JVM options for memory constraints
ENV JAVA_OPTS="-Xmx300m -Xss512k -XX:+UseSerialGC -XX:CICompilerCount=2"

# Expose port
EXPOSE 80

# Run the application with memory constraints
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar