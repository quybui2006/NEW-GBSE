# Bước 1: Build bằng Maven với JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Chạy bằng OpenJDK 21 bản nhẹ (slim)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN mkdir -p uploads logs
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
