FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/email-service
WORKDIR /usr/src/email-service

COPY ./target/email-service.jar /usr/src/email-service

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/usr/src/email-service/email-service.jar"]