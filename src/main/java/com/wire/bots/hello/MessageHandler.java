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

import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.AttachmentMessage;
import com.wire.bots.sdk.models.AudioMessage;
import com.wire.bots.sdk.models.ImageMessage;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.server.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: dejankovacevic
 * Date: 06/09/16
 * Time: 14:01
 */
public class MessageHandler extends MessageHandlerBase {
    private HelloConfig config;

    public MessageHandler(HelloConfig config) {
        this.config = config;
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            Logger.info(String.format("Received Text from: %s", msg.getUserId()));

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
    public String getSmallProfilePicture() {
        return null;
    }

    @Override
    public String getBigProfilePicture() {
        return null;
    }

    @Override
    public boolean onNewBot(NewBot newBot) {
        Logger.info(String.format("onNewBot: user: %s/%s, locale: %s",
                newBot.origin.id,
                newBot.origin.name,
                newBot.locale));

        return true;  // return false in case you don't want to allow this user to open new conv with your bot
    }

    @Override
    public void onNewConversation(WireClient client) {
        try {
            Logger.info(String.format("onNewConversation: conv: %s",
                    client.getId()));

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
                Logger.info(String.format("onMemberJoin: user: %s/%s, bot: %s",
                        user.id,
                        user.name,
                        client.getId()));

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
}
