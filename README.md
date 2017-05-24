# Wire™

[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

# Open source

The [privacy page](https://wire.com/privacy/) and the [privacy](https://wire.com/resource/Wire%20Privacy%20Whitepaper/download/) and [security](https://wire.com/resource/Wire%20Security%20Whitepaper/download/) whitepapers explain the details of the encryption algorithms and protocols used.

For licensing information, see the attached LICENSE file and the list of third-party licenses at [wire.com/legal/licenses/](https://wire.com/legal/licenses/).

If you compile the open source software that we make available from time to time to develop your own mobile, desktop or web application, and cause that application to connect to our servers for any purposes, we refer to that resulting application as an “Open Source App”.  All Open Source Apps are subject to, and may only be used and/or commercialized in accordance with, the Terms of Use applicable to the Wire Application, which can be found at https://wire.com/legal/#terms.  Additionally, if you choose to build an Open Source App, certain restrictions apply, as follows:

a. You agree not to change the way the Open Source App connects and interacts with our servers; b. You agree not to weaken any of the security features of the Open Source App; c. You agree not to use our servers to store data for purposes other than the intended and original functionality of the Open Source App; d. You acknowledge that you are solely responsible for any and all updates to your Open Source App. 

For clarity, if you compile the open source software that we make available from time to time to develop your own mobile, desktop or web application, and do not cause that application to connect to our servers for any purposes, then that application will not be deemed an Open Source App and the foregoing will not apply to that application.

No license is granted to the Wire trademark and its associated logos, all of which will continue to be owned exclusively by Wire Swiss GmbH. Any use of the Wire trademark and/or its associated logos is expressly prohibited without the express prior written consent of Wire Swiss GmbH.

# Hello Bot
This is demo project that uses: [java-bot-sdk](https://github.com/wireapp/bot-sdk). It creates a Bot that will echo everything 
you send it.


## Build the project
 Modify the `Makefile` before the run in order to better reflect your company's name/country...

 Run:
 ```
 make
 ```
 *linux*, *windows* and *darwin* are supported. Running `make` for the first time will generate *self signed certificate* (stored in `./certs` folder). 

## Register as Bot Developer and create some bots
 Go to https://wire.com/b/devbot and log in with your Wire credentials - "DevBot" is the bot to help you setup your developer account and create your own bots.

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
