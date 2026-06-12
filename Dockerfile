# Etapa de compilación
FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle clean build --no-daemon -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080

# Ejecutar como usuario sin privilegios (no root) para contener el impacto de una intrusión
RUN useradd -r -u 1001 appuser
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar /app.jar
USER appuser

ENTRYPOINT ["java", "-jar", "/app.jar"]
