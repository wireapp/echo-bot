#!/usr/bin/env bash
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/echo-bot:latest .
docker push dejankovacevic/echo-bot
kubectl delete pod -l name=echo -n prod
kubectl get pods -l name=echo -n prod
