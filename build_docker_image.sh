#!/bin/bash
export DATETIME=`date +%Y%m%d%H%m%S`
mvn clean
mvn package
#echo docker build -t eam-light-backend:$DATETIME .
docker build -t eam-light-backend:$DATETIME .
echo Starting new image eam-light-backend:$DATETIME
./docker_run.sh eam-light-backend:$DATETIME
