#!/bin/bash

#todo refactor whole file - vars for everything etc etc

# // VARS
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

flag_file="./docker-deploy/script/run/initialized.flag"

base_project_path="marine-unit-monitoring-microservice"

KEYCLOAK_CONTAINER_NAME="keycloak"
KEYCLOAK_IMAGE_NAME="marine-unit-monitoring-microservice-keycloak"
# // VARS

# RUNNING PATH VERIFICATION
if [[ "$(basename "$PWD")" != "$base_project_path" ]]; then
  echo -e "${RED}Error: Script must be called from a base project path: '$base_project_path'.${NC}"
  exit 1
fi

echo -e "${GREEN}Directory check passed. Continuing execution...${NC}"

# // FUNCTIONS
update_containers() {
    local services=("$@")

    echo -e "${YELLOW}STOPPING APPLICATION CONTAINERS:${NC}"
    for service in "${services[@]}"; do
        echo "Stopping $service container."
        docker stop "$service"
    done

    echo -e "${YELLOW}UPDATING APPLICATION IMAGES:${NC}"
    for service in "${services[@]}"; do
        echo "Deleting $service container."
        docker rm "$service"
    done

    for service in "${services[@]}"; do
        echo "Deleting $service image."
        docker rmi "marine-unit-monitoring-microservice-$service"
    done
}

db_init() {
  echo "DATABASES INITIALIZATION"
  scripts=(
        "./docker-deploy/script/db-init/db-init-config.sh"
  )

  for script in "${scripts[@]}"; do
      echo "Running $script..."
      if ! "$script"; then
          echo "Error: $script failed."
          return 1
      fi
  done

  return 0
}
# // FUNCTIONS


# RUNNING
echo -e "#### ${YELLOW}RUN.SH${NC} ####"

# CLEARING SECTION
echo -e "${YELLOW}PRUNING:${NC}"
echo "Removing not used volumes"
docker volume prune -f
echo "Removing not used images"
docker image prune -f

# UPDATING SECTION
echo -e "${YELLOW}Do you want to update mum-commons? (y/n)${NC}"
read -r response

if [[ "$response" == "y" ]]; then
  echo "Updating mum-commons"

  mvn clean install -f ./mum-commons/pom.xml
fi

echo -e "${YELLOW}Do you want to update login-services-reps? (y/n)${NC}"
read -r response

if [[ "$response" == "y" ]]; then
  echo "Updating login-services-reps"

  mvn clean install -f ./login-services-reps/pom.xml
fi

echo -e "${YELLOW}Do you want to update Keycloak SPI .jar? (y/n)${NC}"
read -r response

if [[ "$response" == "y" ]]; then
  echo "Updating Keycloak SPIs"

  mvn clean package -f ./keycloak-spi-bundle/pom.xml

  docker rm -f "$KEYCLOAK_CONTAINER_NAME"
  echo "Keycloak container removed."

  docker rmi "$KEYCLOAK_IMAGE_NAME"
  echo "Keycloak image removed."
else
  echo "Operation canceled."
fi


echo -e "${YELLOW}Enter the names of the services you want to update (space-separated), or press Enter to skip:${NC}"
read -r services_input

IFS=' ' read -r -a services <<< "$services_input"

if [[ ${#services[@]} -eq 0 ]]; then
  echo -e "${YELLOW}Skipping application update.${NC}"
fi

# Verify that the given service names are correct
for service in "${services[@]}"; do
  if ! docker ps -a --format "{{.Names}}" | grep -wq "$service"; then
    echo -e "${RED}Warning: Docker container '$service' does not exist. Check for typos.${NC}"
  fi

  if ! mvn help:evaluate -pl "$service" -Dexpression=project.artifactId -q -DforceStdout > /dev/null 2>&1; then
    echo -e "${RED}Error: Maven module '$service' does not exist. Check for typos.${NC}"
    exit 1
  fi
done

echo -e "${YELLOW}UPDATING TARGETS (.jar-s):${NC}"
for service in "${services[@]}"; do
  if ! mvn clean -pl "$service"; then
    echo -e "${RED}Error: Something went wrong on mvn clean for $service, exiting.${NC}"
    exit 1
  fi

  if ! mvn package -pl "$service"; then
    echo -e "${RED}Error: Something went wrong on mvn package for $service, exiting.${NC}"
    exit 1
  fi
done

echo "PREPARING CONTAINERS FOR AN UPDATE:"
update_containers "${services[@]}"

echo -e "${GREEN}Application updated successfully. Ready for build.${NC}"

# COMPOSING SECTION
echo -e "${YELLOW}DOCKER COMPOSE:${NC}"
docker-compose up -d

# INITIALIZING SECTION
if [ -f "$flag_file" ]; then
  echo -e "${GREEN}Initialization already completed. Skipping execution.${NC}"
  exit 0
fi

if db_init; then
    echo "${GREEN}Creating initialized.flag file.${NC}"
    touch "$flag_file"
else
    echo "${RED}Initialization failed. Flag file not created.${NC}"
    exit 1
fi