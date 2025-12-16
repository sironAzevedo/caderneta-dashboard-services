FROM --platform=$BUILDPLATFORM maven:3-amazoncorretto-21 AS build

WORKDIR /app

# Copia o JAR específico (ajuste o nome conforme seu projeto)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar


ENTRYPOINT ["java", "-jar", "/app/app.jar"]