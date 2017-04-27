//
// Wire
// Copyright (C) 2016 Wire Swiss GmbH
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.
//

package com.wire.bots.github;

import com.wire.bots.github.utils.SessionIdentifierGenerator;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.Util;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.TextMessage;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MessageHandler extends MessageHandlerBase {
    private final BotConfig config;
    private final SessionIdentifierGenerator sesGen = new SessionIdentifierGenerator();

    public MessageHandler(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onNewConversation(WireClient client) {
        try {
            String host = config.host;
            String secret = sesGen.next(6);
            String botId = client.getId();

            Util.writeLine(secret, new File(String.format("%s/%s/secret", config.getCryptoDir(), botId)));
            client.sendText(getHelp(host, secret, botId), TimeUnit.MINUTES.toMillis(15));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            if (msg.getText().equalsIgnoreCase("/help")) {
                String host = config.host;
                String botId = client.getId();
                String secret = Util.readLine(new File(String.format("%s/%s/secret", config.getCryptoDir(), botId)));

                client.sendText(getHelp(host, secret, botId), TimeUnit.SECONDS.toMillis(60));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getLocalizedMessage());
        }
    }

    private String getHelp(String host, String secret, String botId) {
        return String.format("Hi, I'm GitHub-Bot. Here is how to set me up:\n\n"
                        + "1. Go to the repository that you want to connect to\n"
                        + "2. Go to Settings / Webhooks / Add webhook\n"
                        + "3. Add Payload URL: https://%s/github/%s\n"
                        + "4. Set Content-Type: application/json\n"
                        + "5. Set Secret: %s",
                host,
                botId,
                secret);
    }

    @Override
    public void onBotRemoved(String botId) {
        Logger.info("This bot got removed from the conversation :(. BotId: " + botId);
    }
}
