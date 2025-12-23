# ===== Build =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . /workspace
RUN chmod +x mvnw
RUN ./mvnw -DskipTests clean package

# ===== Runtime =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia o jar gerado (ajuste o padrão se o nome não for *SNAPSHOT.jar)
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
