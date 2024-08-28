echo "INITIALIZING MONGODB FOR API-SERVICE"

source ./docker-deploy/script/db-init/mongodb-init-funcs.sh

mongodb_container="api-service-mongodb-primary"
mongors_init_file="/db-init/rs-init.sh"

wait_for_status check_container_running "$RUNNING_STATUS" $mongodb_container

wait_for_status check_file_exists "$EXISTS_STATUS" $mongodb_container "$mongors_init_file"

docker exec $mongodb_container $mongors_init_file

wait_for_status is_mongo_instance_primary "$PRIMARY_STATUS" $mongodb_container

process_init_file $mongodb_container