Wire bot API is currently in alpha.

1. Clone https://github/wireapp/hello-bot

2. Run make

3. Go to https://wire.com/b/devbot (not supported on mobile yet) - "DevBot" is a bot to set up your developer account and create your own bots.

4. Register
  - Email - This is a separate developer account, you can reuse the same email (if you've added email to your Wire account)
  - Website
  - Developer description (e.g. “Pied Piper”)
  - Verification email
  - Account review by Wire
  - Account approved email

5. Create new bot (with DevBot, type "/help" for available commands)
  - Name - name of the bot, will also be used as the URL for the bot
  - Base URL
  - Description
  - RSA key (from ./hello-bot/certs/pubkey.pem)

6. Update config file (with auth_token from DevBot)

7. Deploy service to cloud - You'll need to host it on your own servers.

8. Enable bot (with DevBot) - one of DevBot's commands to activate a bot. 
