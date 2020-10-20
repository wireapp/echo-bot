#!/usr/bin/env bash
docker build -t $DOCKER_USERNAME/echo-bot:latest .
docker push $DOCKER_USERNAME/echo-bot
kubectl delete pod -l name=echo -n prod
kubectl get pods -l name=echo  -n prod
