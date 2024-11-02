#!/bin/bash

# shellcheck disable=SC2034
readonly RUNNING_STATUS="running"
check_container_running() {
  local container="$1"

  inst_status=$(docker container inspect "$container" | jq -r '.[].State.Status')
  echo "$inst_status"
}

# shellcheck disable=SC2034
readonly EXISTS_STATUS="exists"
check_file_exists() {
  local container="$1"
  local file_path="$2"

  docker exec "$container" test -e "$file_path" && echo "exists" || echo "not_exists"
}

# shellcheck disable=SC2034
readonly PRIMARY_STATUS="primary"
is_mongo_instance_primary() {
  local mongodb_rs_container="$1"

  status=$(docker exec "$mongodb_rs_container" mongosh --quiet --eval "db.isMaster().ismaster ? 'primary' : 'not_primary'")
  echo "$status"
}

wait_for_status() {
  local check_function=$1
  local desired_status=$2
  shift 2

  current_status="$($check_function "$@")"
  while [ "$current_status" != "$desired_status" ]; do
    echo "Current status: $current_status. Desired status: $desired_status. Checking again in 5 seconds..."
    sleep 5
    current_status="$($check_function "$@")"
  done
}

# shellcheck disable=SC2034
readonly MONGO_INIT_FILE_PATH="/db-init/mongo-init.js"
process_mongosh_script_file() {
  local mongodb_container="$1"
  local mongosh_script_file_path="$2"

  wait_for_status check_file_exists "$EXISTS_STATUS" "$mongodb_container" "$mongosh_script_file_path"
  docker exec "$mongodb_container" mongosh "$mongosh_script_file_path"
}