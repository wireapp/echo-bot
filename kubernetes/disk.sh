#!/bin/bash

NAME="github-disk"

gcloud compute disks create $NAME \
    --zone europe-west1-c \
    --size 1GB \
    --type pd-ssd