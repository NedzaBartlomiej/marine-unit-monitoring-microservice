---

## ENV Files Documentation (default location from main catalog -> ./docker-deploy/envs)

### `.env` - Main Common Environment File
This file contains common environment variables used across multiple services.

```.env
# Eureka Discovery Configuration
eureka.client.service-url.defaultZone=http://eureka-discovery:8761/eureka/
```

### `<service-name>.env` - Environment File for Particular Service
These files contain service-specific environment variables for individual services.

#### `gateway.env`

```gateway.env
# Eureka Discovery Configuration
eureka.instance.hostname=gateway
# Debug Configuration
_JAVA_OPTIONS:"-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

#### `api-service.env`

```api-service.env
# Eureka Discovery Configuration
eureka.instance.hostname=api-service
# Debug Configuration
_JAVA_OPTIONS:"-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006"
```

#### `dev-service.env`

```dev-service.env
# Eureka Discovery Configuration
eureka.instance.hostname=dev-service
# Debug Configuration
_JAVA_OPTIONS:"-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007"
```

#### `protection-service.env`

```protection-service.env
# Eureka Discovery Configuration
eureka.instance.hostname=protection-service
# Debug Configuration
_JAVA_OPTIONS:"-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5008"
```

#### `email-service.env`

```email-service.env
# Eureka Discovery Configuration
eureka.instance.hostname=email-service
# Debug Configuration
_JAVA_OPTIONS:"-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5009"
```

### `keycloak.env` - Environment File for Keycloak Configuration and Keycloak Database Values
This file contains environment variables specific to the Keycloak service and its database configuration.

```keycloak.env
# Keycloak Database Configuration
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=

# Keycloak Container Secrets
KEYCLOAK_ADMIN=
KEYCLOAK_ADMIN_PASSWORD=

# Keycloak Container Configuration
KC_HOSTNAME_PORT=8080
KC_HTTP_ENABLED=true
KC_HOSTNAME_STRICT_HTTPS=false
KC_HEALTH_ENABLED=true
KC_DB=postgres
KC_DB_URL=jdbc:postgresql://keycloak-postgres-db/${POSTGRES_DB}
KC_DB_USERNAME=${POSTGRES_USER}
KC_DB_PASSWORD=${POSTGRES_PASSWORD}
```

---

### Secrets file for api-service (default location from api-service catalog -> ./src/main/resources/secrets.yaml)

This file contains secrets and API-related configurations for the api-service.

```secrets.yaml
# Mail Configuration
mail:
  username=
  password=

# AIS API Configuration
ais-api:
  auth:
    client-id=
    scope=ais
    client-secret=
    grant-type=client_credentials
    url=https://id.barentswatch.no/connect/token
  latest-ais-url=https://live.ais.barentswatch.no/v1/latest/combined?modelType=Full&modelFormat=Geojson
  latest-ais-bymmsi-url=https://live.ais.barentswatch.no/v1/latest/combined

# Geocode API Configuration
geocode-api:
  api-base-url=https://geocode.search.hereapi.com/v1/geocode
  api-key=
  
# Keycloak Configuration
keycloak:
  client-secret:
```

### External API References for API Service

- **AIS API**: [AIS API Documentation](https://appDeveloperEntity.barentswatch.no/)
- **HERE Geocode and Search API
  **: [HERE API Documentation](https://www.here.com/docs/bundle/geocoding-and-search-api-appDeveloperEntity-guide/page/topics-api/code-geocode-examples.html)

---

--- 

### Secrets file for dev-service (default location from dev-service catalog -> ./src/main/resources/secrets.yaml)

This file contains secrets and API-related configurations for the dev-service.

```secrets.yaml
# Keycloak Configuration
keycloak:
  client-secret:
```

---

--- 

### Secrets file for email-service (default location from email-service catalog -> ./src/main/resources/secrets.yaml)

This file contains secrets and API-related configurations for the email-service.

```secrets.yaml
# Mail Configuration
mail:
  username=
  password=
```

---

--- 

### Secrets file for protection-service (default location from protection-service catalog -> ./src/main/resources/secrets.yaml)

This file contains secrets and API-related configurations for the protection-service.

```secrets.yaml
# Keycloak Configuration
keycloak:
  client-secret: 

# IPInfo API Configuration
ip-info-api:
  access-token: 
```

---