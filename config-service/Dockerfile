# Build stage
FROM maven:3.6.0-jdk-8-slim AS build
COPY src /tmp/src
COPY pom.xml /tmp/
RUN mvn -f /tmp/pom.xml clean package

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /tmp/target/config-service.jar config-service.jar
ENTRYPOINT ["java","-jar","/config-service.jar"]
EXPOSE 5000
