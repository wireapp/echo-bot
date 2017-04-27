#!/bin/bash

NAME="github"

(cd ..; mvn -Plinux package)
docker build --tag wire/$NAME -f ../Dockerfile ../.
docker tag wire/$NAME:latest eu.gcr.io/wire-bot/$NAME
gcloud docker -- push eu.gcr.io/wire-bot/$NAME
kubectl delete pod -l name=$NAME
kubectl get pod -l name=$NAME

