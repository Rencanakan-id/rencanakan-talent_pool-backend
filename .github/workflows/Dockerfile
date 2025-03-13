# Use a lightweight JDK image
FROM eclipse-temurin:21-jdk-alpine

# Create a non-root user
RUN adduser -D -g '' nonRootUser

# Set working directory
WORKDIR /app

# Copy the JAR file into the container
COPY ./talentPool-0.0.1-SNAPSHOT.jar /app/talentPool-0.0.1-SNAPSHOT.jar

# Set file permissions for the non-root user
RUN chown nonRootUser:nonRootUser /app/talentPool-0.0.1-SNAPSHOT.jar && \
    chmod 755 /app/talentPool-0.0.1-SNAPSHOT.jar

# Switch to the non-root user
USER nonRootUser

# Expose the application port
EXPOSE 8080

# Define default command to run the application
CMD ["java", "-jar", "talentPool-0.0.1-SNAPSHOT.jar"]