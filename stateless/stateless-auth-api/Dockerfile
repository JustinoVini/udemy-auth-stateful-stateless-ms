# Estágio 1: Construção da aplicação com Maven
FROM maven:3.8.4-openjdk-17-slim as build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Estágio 2: Imagem final
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT java -jar app.jar