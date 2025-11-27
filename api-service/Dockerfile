FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/api-service.jar .

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "api-service.jar"]