FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/api-service
WORKDIR /usr/src/api-service

COPY ./target/api-service.jar /usr/src/api-service

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/src/api-service/api-service.jar"]