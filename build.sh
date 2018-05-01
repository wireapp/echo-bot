#!/usr/bin/env bash
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/echo-bot:1.5.0 .
docker push dejankovacevic/echo-bot
kubectl delete pod -l name=echo -n staging
kubectl get pods -l name=echo  -n staging
