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

import com.codahale.metrics.MetricRegistry;
import com.waz.model.Messages;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.Util;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.AttachmentMessage;
import com.wire.bots.sdk.models.AudioMessage;
import com.wire.bots.sdk.models.ImageMessage;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.server.model.User;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class MessageHandler extends MessageHandlerBase {
    private final BotConfig config;
    private final MetricRegistry metrics;

    public MessageHandler(BotConfig config, Environment env) {
        this.config = config;
        metrics = env.metrics();
    }

    @Override
    public void onNewConversation(WireClient client) {
        try {
            String host = "[host]";
            String secret = "random";
            Util.writeLine(secret, new File(String.format("%s/%s/secret", config.getCryptoDir(), client.getId())));
            client.sendText(String.format("Hi, I'm GitHub-Bot. Here is how to set me up:\n\n"
                            + "1. Go to the repository that you want to connect to\n\n"
                            + "2. Go to Settings / Webhooks / Add webhook\n\n"
                            + "3. Add Payload URL: https://%s/%s\n\n"
                            + "4. Set Content-Type: application/json\n\n"
                            + "5. Set Secret: %s",
                    host,
                    client.getId(),
                    secret));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onBotRemoved(String botId) {
        Logger.info("This bot got removed from the conversation :(. BotId: " + botId);
    }
}
