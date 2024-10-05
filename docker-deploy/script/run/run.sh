#!/bin/bash

# // VARS
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

flag_file="./docker-deploy/script/run/initialized.flag"
# // VARS

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

# UPDATING APP CODE SECTION
echo -e "${YELLOW}Enter the names of the services you want to update (space-separated), or press Enter to skip:${NC}"
read -r services_input

IFS=' ' read -r -a services <<< "$services_input"

if [[ ${#services[@]} -eq 0 ]]; then
  echo -e "${YELLOW}Skipping application update.${NC}"
fi

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