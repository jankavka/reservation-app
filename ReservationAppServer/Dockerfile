#FROM eclipse-temurin:17
FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY target/reservationapp.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

