FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
EXPOSE 8080
ADD target/Connectify.jar Connectify.jar
ENTRYPOINT ["java","-jar","/Connectify.jar"]