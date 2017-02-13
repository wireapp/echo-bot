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

package com.wire.bots.hello;

import com.codahale.metrics.MetricRegistry;
import com.waz.model.Messages;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.AttachmentMessage;
import com.wire.bots.sdk.models.AudioMessage;
import com.wire.bots.sdk.models.ImageMessage;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.server.model.User;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class MessageHandler extends MessageHandlerBase {
    private final HelloConfig config;
    private final MetricRegistry metrics;

    public MessageHandler(HelloConfig config, Environment env) {
        this.config = config;
        metrics = env.metrics();
    }

    /**
     * @param newBot Initialization object for new Bot instance
     *               -  id          : The unique user ID for the bot.
     *               -  client      : The client ID for the bot.
     *               -  origin      : The profile of the user who requested the bot, as it is returned from GET /bot/users.
     *               -  conversation: The conversation as seen by the bot and as returned from GET /bot/conversation.
     *               -  token       : The bearer token that the bot must use on inbound requests.
     *               -  locale      : The preferred locale for the bot to use, in form of an IETF language tag.
     * @return If TRUE is returned new bot instance is created for this conversation
     * If FALSE is returned this service declines to create new bot instance for this conversation
     */
    @Override
    public boolean onNewBot(NewBot newBot) {
        Logger.info(String.format("onNewBot: bot: %s, origin: %s",
                newBot.id,
                newBot.origin.id));

        return true;
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            Logger.info(String.format("Received Text. bot: %s, from: %s", client.getId(), msg.getUserId()));

            // send echo back to user
            client.sendText("You wrote: " + msg.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImage(WireClient client, ImageMessage msg) {
        try {
            Logger.info(String.format("Received Image: type: %s, size: %,d KB, h: %d, w: %d, tag: %s",
                    msg.getMimeType(),
                    msg.getSize() / 1024,
                    msg.getHeight(),
                    msg.getWidth(),
                    msg.getTag()
            ));

            // echo this image back to user
            byte[] img = client.downloadAsset(msg.getAssetKey(), msg.getAssetToken(), msg.getSha256(), msg.getOtrKey());
            client.sendPicture(img, msg.getMimeType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudio(WireClient client, AudioMessage msg) {
        try {
            Logger.info(String.format("Received Audio: name: %s, type: %s, size: %,d KB, duration: %,d sec",
                    msg.getName(),
                    msg.getMimeType(),
                    msg.getSize() / 1024,
                    msg.getDuration() / 1000
            ));

            // echo this audio back to user
            byte[] audio = client.downloadAsset(msg.getAssetKey(), msg.getAssetToken(), msg.getSha256(), msg.getOtrKey());
            client.sendAudio(audio, msg.getName(), msg.getMimeType(), msg.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachment(WireClient client, AttachmentMessage msg) {
        try {
            Logger.info(String.format("Received Attachment: name: %s, type: %s, size: %,d KB",
                    msg.getName(),
                    msg.getMimeType(),
                    msg.getSize() / 1024
            ));

            // echo this file back to user
            byte[] bytes = client.downloadAsset(msg.getAssetKey(), msg.getAssetToken(), msg.getSha256(), msg.getOtrKey());
            File tempFile = File.createTempFile("hello-bot", "attachment", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(bytes);
            }
            client.sendFile(tempFile, "application/pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return config.getName();
    }

    @Override
    public int getAccentColour() {
        return config.getAccent();
    }

    @Override
    public void onNewConversation(WireClient client) {
        try {
            Logger.info(String.format("onNewConversation: bot: %s, conv: %s",
                    client.getId(),
                    client.getConversationId()));

            client.sendText("Hello! I am Echo. I echo everything you write");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onMemberJoin(WireClient client, ArrayList<String> userIds) {
        try {
            Collection<User> users = client.getUsers(userIds);
            for (User user : users) {
                Logger.info(String.format("onMemberJoin: bot: %s, user: %s/%s",
                        client.getId(),
                        user.id,
                        user.name
                ));

                // say Hi to new participant
                client.sendText("Hi there " + user.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onMemberLeave(WireClient client, ArrayList<String> userIds) {
        Logger.info(String.format("onMemberLeave: users: %s, bot: %s",
                userIds,
                client.getId()));
    }

    @Override
    public void onBotRemoved(String botId) {
        Logger.info("This bot got removed from the conversation :(. BotId: " + botId);
    }

    /**
     * This is generic method that is called every time something is posted to this conversation.
     *
     * @param client         Thread safe wire client that can be used to post back to this conversation
     * @param userId         User Id for the sender
     * @param genericMessage Generic message as it comes from the BE
     */
    @Override
    public void onEvent(WireClient client, String userId, Messages.GenericMessage genericMessage) {
        if (genericMessage.hasConfirmation()) {
            metrics.meter("engagement.delivery").mark();
        }
        if (genericMessage.hasText()) {
            metrics.meter("engagement.txt.received").mark();
        }
    }
}
