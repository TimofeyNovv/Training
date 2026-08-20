FROM maven:3.9.16-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B -Dmaven.test.skip=true package


FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

COPY --from=build /app/target/CargoFlow-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system cargoflow \
    && useradd --system --gid cargoflow cargoflow

USER cargoflow

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]