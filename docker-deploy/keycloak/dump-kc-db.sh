#!/bin/bash

echo "### DUMP-KC-DB.SH ###"

echo "Provide dump date:"
read -r dump_date

readonly dump_name=keycloak_db_dump_"${dump_date}"

echo "DUMPING KEYCLOAK POSTGRES DB"
docker exec keycloak-postgres-db pg_dump -U kc_user -d keycloak_db -F c -b -v -f /tmp/"${dump_name}.dump"

echo "COPYING DUMP FILE FROM CONTAINER TO STORAGE"
docker cp keycloak-postgres-db:/tmp/"${dump_name}.dump" ./docker-deploy/keycloak/