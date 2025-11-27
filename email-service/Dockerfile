FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/email-service.jar .

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "email-service.jar"]