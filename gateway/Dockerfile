FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/gateway.jar .

EXPOSE 80

ENTRYPOINT ["java", "-jar", "gateway.jar"]