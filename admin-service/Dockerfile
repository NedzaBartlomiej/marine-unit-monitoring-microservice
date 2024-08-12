FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/admin-service
WORKDIR /usr/src/admin-service

COPY ./target/admin-service.jar /usr/src/admin-service

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/usr/src/admin-service/admin-service.jar"]