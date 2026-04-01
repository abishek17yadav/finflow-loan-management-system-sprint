# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy POMs for caching
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY admin-service/pom.xml admin-service/
COPY api-gateway/pom.xml api-gateway/
COPY application-service/pom.xml application-service/
COPY document-service/pom.xml document-service/
COPY eureka-server/pom.xml eureka-server/
COPY config-server/pom.xml config-server/

# Download dependencies (cached)
RUN mvn dependency:go-offline -B

# Build
COPY . .
RUN mvn package -DskipTests -pl admin-service -am

# Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/admin-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
