#!/usr/bin/env bash
mvn package -Plinux -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/echo-bot:1.3.0 .
docker push dejankovacevic/echo-bot
kubectl delete pod -l name=echo
kubectl get pods -l name=echo
