# Build stage
FROM maven:3.6.0-jdk-8-slim AS build
COPY src /tmp/src
COPY pom.xml /tmp/
RUN mvn -f /tmp/pom.xml clean package

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /tmp/target/auth-service.jar auth-service.jar
ENTRYPOINT ["java","-jar","/auth-service.jar"]
EXPOSE 4000
