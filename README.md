# Wire™

[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

## Echo Bot
This is demo project that uses: [lithium](https://github.com/wireapp/lithium). It creates a Bot that will echo everything 
you send it.

# Documentation
[Bot API](https://github.com/wireapp/lithium/wiki)

## Build the project
 Modify the `Makefile` before the run in order to better reflect your company's name/country...

 Run:
 ```
 make
 ```
 *linux*, *windows* and *darwin* are supported. Running `make` for the first time will generate *self signed certificate* (stored in `./certs` folder). 

## Register as Bot Developer and create some bots
 Go to [Don](https://app.wire.com?bot_name=don&bot_provider=d39b462f-7e60-4d88-82e1-44d632f94901&bot_service=7a7e5417-0ea3-4608-a5ff-cb809b93f65a) and log in with your Wire credentials - "Don" is the bot to help you setup your developer account and create your own bots.

- Register as Wire Bot Developer:
  - Email - This is a separate developer account, you can reuse the same email (if you've added an email to your Wire account)
  - Verification email
  - Account review by Wire
  - Account approved email

- Create a new bot (with DevBot, type `/help` for available commands)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL (you can put: `https://[Your_Public_IP]:4443`)
  - Description
  - Copy and paste the RSA key (found in `certs/pubkey.pem`)

- Update the `conf/echo.yaml` file (with the *auth_token* you received from DevBot)

- Deploy the service online - You'll need to host it on your own servers.
  Please download the strong cryptography policies for Java from:
	  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
    and unpack the content into `${JAVA_HOME}/jre/lib/security/`

## Deployment
Deploy:
```
target/echo.jar
conf/echo.yaml
keystore.jks
```
files to your server. Notice that you will need a **Public IP** to serve as endpoint that will be called by the Wire Backend

## Start your Bot Service
Run:
```
java -jar /path/to/echo.jar server /path/to/echo.yaml
```

## Enable your bot
Enable bot (with DevBot) - one of DevBot's commands to activate a bot.

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
$ kubectl create secret generic echo-knows \
>  --from-literal=token=$AUTH_TOKEN \
>  --from-literal=keystore_password=$KEYSTORE_PASSWORD
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
