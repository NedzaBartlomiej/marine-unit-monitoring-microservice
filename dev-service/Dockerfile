FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/dev-service
WORKDIR /usr/src/dev-service

COPY ./target/dev-service.jar /usr/src/dev-service

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/usr/src/dev-service/dev-service.jar"]