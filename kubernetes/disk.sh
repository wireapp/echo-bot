#!/bin/bash

NAME="cryptobox-echo"

gcloud compute disks create $NAME \
    --zone europe-west1-c \
    --size 1GB \
    --type pd-ssd