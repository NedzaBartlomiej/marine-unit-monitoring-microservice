FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/gateway
WORKDIR /usr/src/gateway

COPY ./target/gateway.jar /usr/src/gateway

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/usr/src/gateway/gateway.jar"]