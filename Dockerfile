# Stage 1: build
# Start with Maven image that include JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn package -DskipTests

# State 2: create image
# Start with Amazon Correcto JDK17
FROM amazoncorretto:17.0.17

# Set working folder to App and copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

#Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
