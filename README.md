**ENV FILES DOCUMENTATION**
- .env - Main common env file:
# eureka discovery config
+ eureka.client.service-url.defaultZone=


- <service-name>.env - Env file for particular service:
+ + gateway.env:
# eureka discovery config
eureka.instance.hostname=gateway

+ api-service.env:
# eureka discovery config
eureka.instance.hostname=api-service

+ admin-service.env:
# eureka discovery config
eureka.instance.hostname=admin-service

+ dev-service.env:
# eureka discovery config
eureka.instance.hostname=dev-service


- keycloak.env - Env file for keycloak config and keycloak database values:

# keycloak_db
+ POSTGRES_DB=
+ POSTGRES_USER=
+ POSTGRES_PASSWORD=

# kecyloak container secrets
+ KEYCLOAK_ADMIN=
+ KEYCLOAK_ADMIN_PASSWORD=

# keycloak container
+ KC_HOSTNAME_PORT=8080
+ KC_HTTP_ENABLED=true
+ KC_HOSTNAME_STRICT_HTTPS=false
+ KC_HEALTH_ENABLED=true
+ KC_DB=postgres
+ KC_DB_URL=jdbc:postgresql://keycloak-postgres-db/${POSTGRES_DB}
+ KC_DB_USERNAME=${POSTGRES_USER}
+ KC_DB_PASSWORD=${POSTGRES_PASSWORD}
