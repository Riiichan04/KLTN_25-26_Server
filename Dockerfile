FROM gradle:9.3.1-jdk21 AS build
WORKDIR /app

COPY build.gradle* build.gradle.kts* settings.gradle* settings.gradle.kts* gradle.properties* ./
COPY gradle ./gradle

COPY src ./src

RUN gradle clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]