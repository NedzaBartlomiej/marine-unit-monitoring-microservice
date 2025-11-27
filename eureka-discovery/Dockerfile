FROM amazoncorretto:21-alpine-jdk

LABEL author="Bartlomiej Nedza"

RUN mkdir -p /app
WORKDIR /app

COPY ./target/eureka-discovery.jar .

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "eureka-discovery.jar"]