FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/dev-service.jar .

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "dev-service.jar"]