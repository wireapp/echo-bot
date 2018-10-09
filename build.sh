#!/usr/bin/env bash
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t $DOCKER_USERNAME/echo-bot:latest .
docker push $DOCKER_USERNAME/echo-bot
kubectl delete pod -l name=echo
kubectl get pods -l name=echo
