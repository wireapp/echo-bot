#!/bin/bash

SECRET_NAME="echo-knows"
AUTH_TOKEN="your_token"
KEYSTORE_PASSWORD="your_secret"

kubectl delete secret $SECRET_NAME
kubectl create secret generic $SECRET_NAME \
    --from-literal=token=$AUTH_TOKEN \
    --from-literal=keystore_password=$KEYSTORE_PASSWORD