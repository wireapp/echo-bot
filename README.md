# Wire™

[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

## Echo Bot
This is demo project that uses: [lithium](https://github.com/wireapp/lithium). It creates a Bot that will echo everything 
you send it.

# Documentation
[Bot API](https://github.com/wireapp/lithium/wiki)

## Build the project
 Run:
 ```
 make
 ```
 *linux*, *windows* and *darwin* are supported.

## Register as Bot Developer and create some bots
- Run `myprovider.sh`
- You can use `myprovider.sh` to generate your self signed cert that can be used for your service
- Update the `conf/echo.yaml` file (with the *auth_token* for your service)

- Deploy the service online - You'll need to host it on your own servers.
  Please download the strong cryptography policies for Java from:
	  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
    and unpack the content into `${JAVA_HOME}/jre/lib/security/`

## Deployment
Deploy:
```
target/echo.jar
conf/echo.yaml
```
files to your server. Notice that you will need a **Public IP** to serve as endpoint that will be called by the Wire Backend

## Start your Bot Service
Run:
```
java -jar /path/to/echo.jar server /path/to/echo.yaml
```

## Enable your bot
Enable bot (with `myprovider.sh` command)

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
