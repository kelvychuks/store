FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=build /app/target/store-1.0.0.jar store.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","store.jar"]