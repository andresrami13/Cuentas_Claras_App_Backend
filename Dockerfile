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

# Memoria de la JVM (techo duro, no porcentaje del contenedor):
#  -Xms256m -Xmx512m  cap fijo de heap en 512 MB. Para la carga actual (pocos
#                     usuarios) sobra; evita que la JVM escale el heap hasta el
#                     70% del contenedor y se quede en una meseta de ~2 GB.
#  -XX:+UseSerialGC   GC de un solo hilo, menor overhead de memoria que G1 (el
#                     default de JDK 17) en heaps pequeños.
#  -Xss512k           reduce el stack por hilo (default 1 MB); ahorra RAM con
#                     muchos hilos del pool de Tomcat.
# Si aparece OutOfMemoryError en logs, subir -Xmx a 768m (no volver a MaxRAMPercentage).
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+UseSerialGC", "-Xss512k", "-jar", "/app.jar"]
