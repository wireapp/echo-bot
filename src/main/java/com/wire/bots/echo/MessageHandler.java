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

package com.wire.bots.echo;

import com.wire.blender.Blender;
import com.wire.xenon.MessageHandlerBase;
import com.wire.xenon.WireClient;
import com.wire.xenon.assets.*;
import com.wire.xenon.backend.models.Member;
import com.wire.xenon.backend.models.NewBot;
import com.wire.xenon.backend.models.SystemMessage;
import com.wire.xenon.backend.models.User;
import com.wire.xenon.models.*;
import com.wire.xenon.state.State;
import com.wire.xenon.tools.Logger;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler extends MessageHandlerBase {

    /*
    Only for calling
     */
    private final ConcurrentHashMap<UUID, Blender> blenders = new ConcurrentHashMap<>();

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
    public boolean onNewBot(NewBot newBot, String token) {
        Logger.info(String.format("onNewBot: bot: %s, username: %s",
                newBot.id,
                newBot.origin.handle));

        for (Member member : newBot.conversation.members) {
            if (member.service != null) {
                Logger.warning("Rejecting NewBot. Provider: %s service: %s",
                        member.service.providerId,
                        member.service.id);
                return false; // we don't want to be in a conv if other bots are there.
            }
        }
        return true;
    }

    @Override
    public void onNewConversation(WireClient client, SystemMessage message) {
        try {
            Logger.info("onNewConversation: bot: %s, conv: %s",
                    client.getId(),
                    client.getConversationId());

            String label = "Hello! I am Echo. I echo everything you post here";
            client.send(new MessageText(label));
        } catch (Exception e) {
            Logger.error("onNewConversation: %s", e);
        }
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            UUID botId = client.getId();
            UUID userId = msg.getUserId();
            Logger.info("Received Text '%s' from: %s, conv: %s, msg: %s @%s",
                    msg.getText(),
                    userId,
                    msg.getConversationId(),
                    msg.getMessageId(),
                    msg.getTime());

            final User user = client.getUser(msg.getUserId());

            String text = String.format("@%s _%s_", user.handle, msg.getText());

            // send echo back to user, mentioning this user
            MessageText t = new MessageText(text);
            t.addMention(userId, 0, user.handle.length() + 1);

            client.send(t);

            Logger.info("Text sent back in conversation: %s, messageId: %s, bot: %s",
                    client.getConversationId(),
                    t.getMessageId(),
                    botId);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("onText: %s", e);
        }
    }

    @Override
    public void onImage(WireClient client, ImageMessage msg) {
        try {
            Logger.info("Received Image: type: %s, size: %,d KB, h: %d, w: %d",
                    msg.getMimeType(),
                    msg.getSize() / 1024,
                    msg.getHeight(),
                    msg.getWidth()
            );

            // download this image from Wire server
            byte[] img = client.downloadAsset(
                    msg.getAssetKey(),
                    msg.getAssetToken(),
                    msg.getSha256(),
                    msg.getOtrKey());

            // echo this image back to user
            final Picture picture = new Picture(img, msg.getMimeType());

            // first we upload this picture
            final AssetKey assetKey = client.uploadAsset(picture);
            picture.setAssetKey(assetKey.key);
            picture.setAssetToken(assetKey.token);

            // post this picture on Wire
            client.send(picture);
        } catch (Exception e) {
            Logger.error("onImage: %s", e);
        }
    }

    @Override
    public void onAudio(WireClient client, AudioMessage msg) {
        try {
            Logger.info("Received Audio: name: %s, type: %s, size: %,d KB, duration: %,d sec",
                    msg.getName(),
                    msg.getMimeType(),
                    msg.getSize() / 1024,
                    msg.getDuration() / 1000
            );

            // download this audio from Wire Server
            byte[] audio = client.downloadAsset(msg.getAssetKey(),
                    msg.getAssetToken(),
                    msg.getSha256(),
                    msg.getOtrKey());

            // echo this audio back to user
            final AudioPreview preview = new AudioPreview(audio, msg.getName(), msg.getMimeType(), msg.getDuration());
            final AudioAsset audioAsset = new AudioAsset(audio, preview);

            final AssetKey assetKey = client.uploadAsset(audioAsset);
            audioAsset.setAssetKey(assetKey.key);
            audioAsset.setAssetToken(assetKey.token);

            client.send(audioAsset);
        } catch (Exception e) {
            Logger.error("onAudio: %s", e);
        }
    }

    @Override
    public void onVideo(WireClient client, VideoMessage msg) {
        try {
            Logger.info("Received Video: name: %s, type: %s, size: %,d KB, duration: %,d sec",
                    msg.getName(),
                    msg.getMimeType(),
                    msg.getSize() / 1024,
                    msg.getDuration() / 1000
            );

            // download this video from Wire Server
            byte[] video = client.downloadAsset(msg.getAssetKey(),
                    msg.getAssetToken(),
                    msg.getSha256(),
                    msg.getOtrKey());

            // echo this video back to user
            final VideoAsset videoAsset = new VideoAsset(video, msg.getMimeType(), msg.getMessageId());
            final AssetKey assetKey = client.uploadAsset(videoAsset);
            videoAsset.setAssetKey(assetKey.key);
            videoAsset.setAssetToken(assetKey.token);

            client.send(videoAsset);

            // echo preview
            final VideoPreview preview = new VideoPreview(
                    msg.getName(),
                    msg.getMimeType(),
                    msg.getDuration(),
                    msg.getHeight(),
                    msg.getWidth(),
                    (int) msg.getSize(),
                    msg.getMessageId()
            );

            client.send(preview);
        } catch (Exception e) {
            Logger.error("onVideo: %s", e);
        }
    }

    @Override
    public void onText(WireClient client, EphemeralTextMessage msg) {
        try {
            final MessageEphemeral message = new MessageEphemeral(msg.getExpireAfterMillis());
            message.setText("You wrote: " + msg.getText());
            client.send(message);
        } catch (Exception e) {
            Logger.error("onEphemeralText: %s", e);
        }
    }

    @Override
    public void onAttachment(WireClient client, AttachmentMessage attach) {
        try {
            Logger.info("Received Attachment: name: %s, type: %s, size: %,d KB",
                    attach.getName(),
                    attach.getMimeType(),
                    attach.getSize() / 1024
            );

            // echo this file back to user
            UUID messageId = UUID.randomUUID();
            FileAssetPreview preview = new FileAssetPreview(attach.getName(), attach.getMimeType(), attach.getSize(), messageId);
            FileAsset asset = new FileAsset(attach.getAssetKey(), attach.getAssetToken(), attach.getSha256(), attach.getOtrKey(), messageId);

            final AssetKey assetKey = client.uploadAsset(asset);
            asset.setAssetKey(assetKey.key);
            asset.setAssetToken(assetKey.token);

            client.send(preview);
            client.send(asset);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("onAttachment: %s", e);
        }
    }

    @Override
    public void onMemberLeave(WireClient client, SystemMessage msg) {
        for (UUID userId : msg.users) {
            Logger.info("onMemberLeave: user: %s, bot: %s",
                    userId,
                    client.getId());
        }
    }

    @Override
    public void onBotRemoved(UUID botId, SystemMessage msg) {
        Logger.info("Bot: %s got removed by %s from the conversation :(", botId, msg.from);
    }

    @Override
    public void onMemberJoin(WireClient client, SystemMessage msg) {
        try {
            for (UUID userId : msg.users) {
                User user = client.getUser(userId);
                Logger.info("onMemberJoin: bot: %s, user: %s/%s @%s",
                        client.getId(),
                        user.id,
                        user.name,
                        user.handle
                );

                // say Hi to new participant
                client.send(new MessageText("Hi there " + user.name));
            }
        } catch (Exception e) {
            Logger.error("onMemberJoin: %s", e);
        }
    }

    @Override
    public void onConfirmation(WireClient client, ConfirmationMessage msg) {
        Logger.info("onConfirmation: bot: %s. Status for message: %s, sent to user: %s:%s is now: %s",
                client.getId(),
                msg.getConfirmationMessageId(),
                msg.getUserId(),
                msg.getClientId(),
                msg.getType());
    }

    @Override
    public void onPing(WireClient client, PingMessage msg) {
        try {
            UUID userId = msg.getUserId();
            Logger.info("Received a Ping from: %s, conv: %s, msgId: %s @%s",
                    userId,
                    msg.getConversationId(),
                    msg.getMessageId(),
                    msg.getTime());

            final UUID messageId = UUID.randomUUID();
            final FileAssetPreview preview = new FileAssetPreview("SneakyPeeky", "application/octet-stream", 123, messageId);

            final SneakyPeek sneakyPeek = new SneakyPeek(messageId);

            client.send(preview);
            client.send(sneakyPeek);

            Logger.info("Sent Sneak peek to user: %s, conv: %s, msgId: %s",
                    userId,
                    msg.getConversationId(),
                    messageId);
        } catch (Exception e) {
            Logger.error("onPing: %s", e);
        }
    }
    // ***** Calling *****

    @Override
    public void onCalling(WireClient client, CallingMessage msg) {
        UUID botId = client.getId();
        Blender blender = getBlender(botId);
        blender.recvMessage(botId.toString(), msg.getUserId().toString(), msg.getClientId(), msg.getContent());
    }

    private Blender getBlender(UUID botId) {
        return blenders.computeIfAbsent(botId, k -> {
            try {
                Config config = Service.instance.getConfig();
                String module = config.getModule();
                String ingress = config.getIngress();
                int portMin = config.getPortMin();
                int portMax = config.getPortMax();

                State state = Service.instance.getStorageFactory().create(botId);
                NewBot bot = state.getState();
                Blender blender = new Blender();
                blender.init(module, botId.toString(), bot.client, ingress, portMin, portMax);
                blender.registerListener(new CallListener(Service.instance.getRepo()));
                return blender;
            } catch (Exception e) {
                Logger.error(e.toString());
                return null;
            }
        });
    }
    // ***** Calling ****
}
