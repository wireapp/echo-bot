#!/bin/bash

NAME="github-knows"

AUTH_TOKEN="your_token"
KEYSTORE_PASSWORD="your_secret"

kubectl delete secret $NAME
kubectl create secret generic $NAME \
    --from-literal=token=$AUTH_TOKEN \
    --from-literal=keystore_password=$KEYSTORE_PASSWORD