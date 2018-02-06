# Wire™
[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

## Echo Bot
[![Build Status](https://travis-ci.org/wireapp/echo-bot.svg?branch=master)](https://travis-ci.org/wireapp/echo-bot)

This is demo project that uses: [lithium](https://github.com/wireapp/lithium). It creates a Bot that will echo everything
you send it.

# Documentation
[Bot API](https://github.com/wireapp/lithium/wiki)

## Build the project
 Run:
 ```
 mvn -Plinux package
 ```
 *linux*, *windows* and *darwin* are supported.

## Run Bot Service
Run:
```
java -jar /path/to/echo.jar server /path/to/echo.yaml
```

# Build Docker images
	docker build --tag wire/bots.runtime -f Dockerfile.runtime .

	docker build --tag wire/echo -f Dockerfile .

# Tag images (assuming you have created *wire-bot* proj with gcloud already)
    docker tag wire/bots.runtime:latest eu.gcr.io/wire-bot/bots.runtime

    docker tag wire/echo:latest eu.gcr.io/wire-bot/echo

# Push images
    gcloud docker -- push eu.gcr.io/wire-bot/bots.runtime

    gcloud docker -- push eu.gcr.io/wire-bot/echo

# Create ConfigMap from files in `conf` folder
```
$ kubectl create configmap echo-config --from-file=conf
```                                                     

# Create GCE secrets
```
$ kubectl create secret generic echo-knows --from-literal=token=$AUTH_TOKEN
```                                                     

# Create GCE Persistent Disk
```
$ gcloud compute disks create echo-disk \
>  --zone europe-west1-c \
>  --size 1GB \
>  --type pd-ssd
```

# Deploy to GCE
`$ kubectl create -f kubernetes/deployment.yaml`
