#!/bin/bash
if [ "$1" != "" ]; then
	echo Setting the target image to: $1
	TARGET_IMAGE=$1
else
	TARGET_IMAGE="eam-light-backend:20190730140700"
fi
echo TARGET_IMAGE: $TARGET_IMAGE
echo Killing current container
OLD_CONTAINER_ID=$(docker ps | grep "eam-light-backend" | awk '{print $1}')
if [ -z "$OLD_CONTAINER_ID" ]; then
	echo No currently running container found
else
	docker kill $OLD_CONTAINER_ID
	sleep 2
fi
echo Attempting to start the docker container $TARGET_IMAGE
docker run -p 8081:8081 -p 9090:9090 -d --env-file .env $TARGET_IMAGE


echo Sleeping 5 seconds to give it time to start
sleep 5
ADMIN_PASSWORD=$(cat .wildfly_admin_password)
echo Adding the admin user with password ${ADMIN_PASSWORD}
CONTAINER_ID=$(docker ps -l -q)
docker exec $CONTAINER_ID wildfly/bin/add-user.sh admin ${ADMIN_PASSWORD} --silent
echo These are the running containers
docker ps
