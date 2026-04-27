# build stage
FROM gradle:8.14.3-jdk17 AS builder
WORKDIR /app

COPY . .

RUN gradle clean bootJar --no-daemon

# runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 19300

ENTRYPOINT ["java", "-jar", "app.jar"]