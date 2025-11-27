FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/protection-service.jar .

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "protection-service.jar"]