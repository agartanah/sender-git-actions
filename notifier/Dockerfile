# ---------- STAGE 1: build ----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw -B dependency:go-offline

COPY src src

RUN ./mvnw -B clean package -DskipTests


# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]