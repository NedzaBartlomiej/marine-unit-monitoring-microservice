#!/bin/bash

# // VARS
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

flag_file="./docker-deploy/script/start/initialized.flag"
# // VARS

# // FUNCTIONS
update_containers() {
    services=("api-service" "admin-service" "dev-service")

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


# EXECUTION
echo -e "#### ${YELLOW}START.SH${NC} ####"

# CLEARING SECTION
echo -e "${YELLOW}PRUNING:${NC}"
echo "Removing not used volumes"
docker volume prune -f
echo "Removing not used images"
docker image prune -f

# UPDATING APP CODE SECTION
echo -e "${YELLOW}Do you want to update containers? (when u provided any changes in app code choose 'y') (y/n)${NC}"
read -r choice


if [[ "$choice" == "y" || "$choice" == "Y" ]]; then
  echo -e "${YELLOW}UPDATING TARGETS (.jar-s):${NC}"

  if ! mvn clean; then
    echo -e "${RED}Error: Something went wrong on mvn clean, exiting.${NC}"
    exit 1
  fi

  if ! mvn package; then
    echo -e "${RED}Error: Something went wrong on mvn package, exiting.${NC}"
    exit 1
  fi

  echo "PREPARING CONTAINERS FOR AN UPDATE:"
  update_containers

  echo -e "${GREEN}Application updated successfully. Ready for build.${NC}"
else
  echo -e "${YELLOW}Skipping application update.${NC}"
fi

# COMPOSING SECTION
echo -e "${YELLOW}DOCKER COMPOSE:${NC}"
docker-compose up -d

# INITIALIZING SECTION
if [ -f "$flag_file" ]; then
  echo -e "${GREEN}Initialization already completed. Skipping execution.${NC}"
  exit 0
fi

if db_init; then
    echo "Creating initialized.flag file."
    touch "$flag_file"
else
    echo "Initialization failed. Flag file not created."
    exit 1
fi