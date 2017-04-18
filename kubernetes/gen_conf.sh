#!/bin/bash

NAME="echo-config"

kubectl delete configmap $NAME
kubectl create configmap $NAME --from-file=../conf