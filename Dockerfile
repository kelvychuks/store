# --- build stage -----------------------------------------------------------
# Dependencies resolve in their own layer, so a code-only change does not
# re-download the world on every deploy.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- runtime stage ---------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as a non-root user: nothing in this image needs root at runtime.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /build/target/store-1.0.0.jar app.jar

EXPOSE 8080

# Container memory is small on free tiers; let the JVM size itself to the cgroup.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
