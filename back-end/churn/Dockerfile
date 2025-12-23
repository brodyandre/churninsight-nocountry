FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy Maven files and build
COPY back-end/churn/pom.xml .
COPY back-end/churn/mvnw .
COPY back-end/churn/.mvn .mvn

# Build the application
RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests

# Copy the JAR file
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=0 /app/target/churn-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
