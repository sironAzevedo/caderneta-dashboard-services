FROM --platform=$BUILDPLATFORM maven:3-amazoncorretto-21 AS build

WORKDIR /app

# Copia o JAR específico (ajuste o nome conforme seu projeto)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Elastic APM Agent
ADD https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.50.0/elastic-apm-agent-1.50.0.jar /app/elastic-apm-agent.jar
RUN chmod 644 /app/elastic-apm-agent.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]