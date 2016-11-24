###Wire bot API is currently in alpha.

1. Clone this repository: [https://github/wireapp/hello-bot](https://github/wireapp/hello-bot)

2. Run: `$make linux`

3. Go to https://wire.com/b/devbot (not supported on mobile browsers yet) - "DevBot" is a bot to set up your developer account and create your own bots.

4. Register
  - Email - This is a separate developer account, you can reuse the same email (if you've added an email to your Wire account)
  - Website (you can leave it blank: `https://`)
  - Developer description (e.g. “Pied Piper”)
  - Verification email
  - Account review by Wire
  - Account approved email

5. Create new bot (with DevBot, type `/help` for available commands)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL (you can put: `https://[Your_Public_IP]:8050`)
  - Description
  - RSA key (from `./hello-bot/certs/pubkey.pem`)

6. Update config file (with auth_token from DevBot)

7. Deploy service to cloud - You'll need to host it on your own servers.
  - Please download the strong cryptography policies for Java from:
	  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
    and unpack the content into `${JAVA_HOME}/jre/lib/security/`
```  
  - Copy ./target/hello.jar, ./hello.yaml, ./certs/keystore.jks
  - $ mkdir crypto
  - $ java -jar hello.jar server hello.yaml
  - $ curl -i http://localhost:8049/bots/status
  - $ curl -ikv https://localhost:8050/bots/status
  - $ curl -XPOST https://localhost:8051/healthcheck
  - Expose port 8050
```  
8. Enable bot (with DevBot) - one of DevBot's commands to activate a bot. 
