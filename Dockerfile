FROM openjdk:11
LABEL version="1.0" mantainer="github.com/Rundzega"
EXPOSE 8080
ADD target/column-calc-rest.jar column-calc-rest-docker.jar
ENTRYPOINT ["java", "-jar", "column-calc-rest-docker.jar"]
