# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Instalar curl para health checks
RUN apk add --no-cache curl

# Copiar TODOS los archivos de construcción (desde el directorio actual)
COPY . .

# Hacer ejecutable el mvnw
RUN chmod +x mvnw

# Crear directorio para logs
RUN mkdir -p /app/logs

# Compilar la aplicación (SALTAR TESTS COMPLETAMENTE)
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true -DskipITs

# Exponer puerto
EXPOSE 8080

# Salud check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "target/actuator-demo-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]