#!/bin/bash

NAME="github-config"

kubectl delete configmap $NAME
kubectl create configmap $NAME --from-file=../conf