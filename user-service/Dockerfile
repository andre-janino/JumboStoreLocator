# Build stage
FROM maven:3.6.0-jdk-8-slim AS build
COPY src /tmp/src
COPY pom.xml /tmp/
RUN mvn -f /tmp/pom.xml clean package -Dmaven.test.skip=true

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /tmp/target/user-service.jar user-service.jar
ENTRYPOINT ["java","-jar","/user-service.jar"]
EXPOSE 8082