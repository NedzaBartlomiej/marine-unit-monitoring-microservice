FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/protection-service
WORKDIR /usr/src/protection-service

COPY ./target/protection-service.jar /usr/src/protection-service

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/usr/src/protection-service/protection-service.jar"]