FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11
LABEL version="1.0" mantainer="github.com/Rundzega"
COPY --from=build /home/app/target/column-calc-rest.jar /usr/local/lib/column-calc-rest.jar
EXPOSE 8080
ADD target/column-calc-rest.jar column-calc-rest-docker.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/column-calc-rest.jar"]
