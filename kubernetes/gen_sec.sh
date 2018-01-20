#!/bin/bash

NAME="echo-knows"

AUTH_TOKEN="your_token" # Change this

kubectl delete secret $NAME
kubectl create secret generic $NAME \
    --from-literal=token=$AUTH_TOKEN \
