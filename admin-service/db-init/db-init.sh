#!/bin/bash

echo "INITIALIZING MONGODB FOR ADMIN-SERVICE"

source ./docker-deploy/script/db-init/mongodb-init-funcs.sh

mongodb_container="admin-service-mongodb"

wait_for_status check_container_running "$RUNNING_STATUS" $mongodb_container

process_init_file $mongodb_container