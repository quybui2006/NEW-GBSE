# Stage 1: build with Maven on JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: run on a slim JRE 17, as a non-root user
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN mkdir -p uploads logs \
    && useradd -r -u 1001 appuser \

    
    && chown -R appuser /app
COPY --from=build /target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
