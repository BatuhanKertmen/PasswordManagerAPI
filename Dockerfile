# Use an official JDK as the base image
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy dependencies separately to leverage caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Expose the application port
EXPOSE 8080

# Run the application in development mode
CMD ["./mvnw", "spring-boot:run"]
