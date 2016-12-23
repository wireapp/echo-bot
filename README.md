#hello-bot

##Wire bot API is currently in alpha.  
  
1. Clone this repository: [https://github.com/wireapp/wire-bot-java](https://github.com/wireapp/wire-bot-java)

2. Install [Maven](http://maven.apache.org/install.html) and make sure it is added to `PATH`

3. Run: `$make linux`

4. Go to https://wire.com/b/devbot (not supported on mobile browsers yet) and log in with your Wire credentials - "DevBot" is a bot to set up your developer account and create your own bots.

5. Register to the bot service:
  - Email - This is a separate developer account, you can reuse the same email (if you've added an email to your Wire account)
  - Website (you can leave it blank: `https://`)
  - Developer description (e.g. “Pied Piper”)
  - Verification email
  - Account review by Wire
  - Account approved email

6. Create a new bot (with DevBot, type `/help` for available commands)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL (you can put: `https://[Your_Public_IP]:8050`)
  - Description
  - Copy and paste the RSA key (found in `./hello-bot/certs/pubkey.pem`)

7. Update the `hello.yaml` file (with the *auth_token* you received from DevBot)

8. Deploy the service online - You'll need to host it on your own servers.
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

Full Terms and Conditions are in the making.
