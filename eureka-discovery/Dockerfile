FROM openjdk:21

LABEL author="Bartlomiej Nedza"

RUN mkdir /usr/src/eureka-discovery
WORKDIR /usr/src/eureka-discovery

COPY ./target/eureka-discovery.jar /usr/src/eureka-discovery

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/usr/src/eureka-discovery/eureka-discovery.jar"]