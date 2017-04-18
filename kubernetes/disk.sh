#!/bin/bash

gcloud compute disks create cryptobox-echo \
    --zone europe-west1-c \
    --size 1GB \
    --type pd-ssd