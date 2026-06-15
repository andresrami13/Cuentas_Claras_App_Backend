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

# Limita el heap al 70% de la RAM del contenedor (se adapta al límite de Railway).
# Sin esto la JVM reserva hasta el 25% de la RAM del host y no la libera, lo que
# se ve como "consumo extra" aunque la app esté ociosa.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-jar", "/app.jar"]
