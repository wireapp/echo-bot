# Wire™

[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

This repository is part of the source code of Wire. You can find more information at [wire.com](https://wire.com) or by contacting opensource@wire.com.

You can find the published source code at [github.com/wireapp/wire](https://github.com/wireapp/wire).

For licensing information, see the attached LICENSE file and the list of third-party licenses at [wire.com/legal/licenses/](https://wire.com/legal/licenses/).

If you compile the open source software that we make available from time to time to develop your own mobile, desktop or web application, and cause that application to connect to our servers for any purposes, we refer to that resulting application as an “Open Source App”.  All Open Source Apps are subject to, and may only be used and/or commercialized in accordance with, the Terms of Use applicable to the Wire Application, which can be found at https://wire.com/legal/#terms.  Additionally, if you choose to build an Open Source App, certain restrictions apply, as follows:

a. You agree not to change the way the Open Source App connects and interacts with our servers; b. You agree not to weaken any of the security features of the Open Source App; c. You agree not to use our servers to store data for purposes other than the intended and original functionality of the Open Source App; d. You acknowledge that you are solely responsible for any and all updates to your Open Source App.

For clarity, if you compile the open source software that we make available from time to time to develop your own mobile, desktop or web application, and do not cause that application to connect to our servers for any purposes, then that application will not be deemed an Open Source App and the foregoing will not apply to that application.

No license is granted to the Wire trademark and its associated logos, all of which will continue to be owned exclusively by Wire Swiss GmbH. Any use of the Wire trademark and/or its associated logos is expressly prohibited without the express prior written consent of Wire Swiss GmbH.

## wire-bot-java

1. Fork this repository: [https://github.com/wireapp/wire-bot-java](https://github.com/wireapp/wire-bot-java)

2. Run: `$make linux`

3. Go to https://wire.com/b/devbot and log in with your Wire credentials - "DevBot" is a bot to set up your developer account and create your own bots.

4. Register to the bot service:
  - Email - This is a separate developer account, you can reuse the same email (if you've added an email to your Wire account)
  - Website (you can leave it blank: `https://`)
  - Developer description (e.g. “Pied Piper”)
  - Verification email
  - Account review by Wire
  - Account approved email

5. Create a new bot (with DevBot, type `/help` for available commands)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL (you can put: `https://[Your_Public_IP]:8050`)
  - Description
  - Copy and paste the RSA key (found in `./hello-bot/certs/pubkey.pem`)

6. Update the `hello.yaml` file (with the *auth_token* you received from DevBot)

7. Deploy the service online - You'll need to host it on your own servers.
  - Please download the strong cryptography policies for Java from:
	  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
    and unpack the content into `${JAVA_HOME}/jre/lib/security/`
```
  - Create a new directory and copy the following files with that exact directory structure:
  ./target/hello.jar, ./hello.yaml, ./certs/keystore.jks
  - $ mkdir crypto
  - $ java -jar hello.jar server hello.yaml
  - $ curl -i http://localhost:8049/bots/status
  - $ curl -ikv https://localhost:8050/bots/status
  - $ curl http://localhost:8051/healthcheck
  - Expose port 8050
```

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

# Create GCE secrets
```
$ kubectl create secret generic echo-knows \
>  --from-literal=token=$AUTH_TOKEN \
>  --from-literal=keystore_password=$KEYSTORE_PASSWORD
```                                                     
$AUTH_TOKEN must have *Bearer* prefix!

# Create GCE Persistent Disk
```
$ gcloud compute disks create cryptobox-echo \
>  --zone europe-west1-c \
>  --size 1GB \
>  --type pd-ssd
```

# Deploy to GCE
`$ kubectl create -f service.yaml`
`$ kubectl create -f deployment.yaml`
