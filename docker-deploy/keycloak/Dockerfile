FROM quay.io/keycloak/keycloak:latest as builder

WORKDIR /opt/keycloak

ADD --chown=keycloak:keycloak --chmod=644 ../keycloak-spi-bundle/target/keycloak-spi-bundle.jar /opt/keycloak/providers/keycloak-spi-bundle.jar

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--debug", "--log-level=TRACE"]