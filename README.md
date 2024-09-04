---

## ENV Files Documentation

### `.env` - Main Common Environment File
This file contains common environment variables used across multiple services.

```env
# Eureka Discovery Configuration
eureka.client.service-url.defaultZone=
```

### `<service-name>.env` - Environment File for Particular Service
These files contain service-specific environment variables for individual services.

#### `gateway.env`

```env
# Eureka Discovery Configuration
eureka.instance.hostname=gateway
```

#### `api-service.env`

```env
# Eureka Discovery Configuration
eureka.instance.hostname=api-service
```

#### `admin-service.env`

```env
# Eureka Discovery Configuration
eureka.instance.hostname=admin-service
```

#### `dev-service.env`

```env
# Eureka Discovery Configuration
eureka.instance.hostname=dev-service
```

### `keycloak.env` - Environment File for Keycloak Configuration and Keycloak Database Values
This file contains environment variables specific to the Keycloak service and its database configuration.

```env
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
