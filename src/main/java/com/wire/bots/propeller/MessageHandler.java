package com.wire.bots.propeller;

import com.wire.wbotz.BotClient;
import com.wire.wbotz.Logger;
import com.wire.wbotz.MessageHandlerBase;
import com.wire.wbotz.models.Message;
import com.wire.wbotz.server.model.Conversation;
import com.wire.wbotz.server.model.NewBot;
import com.wire.wbotz.server.model.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Propeller.ai
 * http://propeller.rocks
 */
public class MessageHandler extends MessageHandlerBase {
    private HelloConfig config;
    private ImageRecognitionClient recognition;
    
    public MessageHandler(HelloConfig config) {
        this.config = config;
        this.recognition = new ImageRecognitionClient();
    }

    /**
     * This callback method is called every time somebody posts something into the conversation
     *
     * @param client BotClient object that can be used to post new content into this conversation
     * @param msg    Message object containing the actual post. All the data is already decrypted.
     */
    @Override
    public void onMessage(BotClient client, Message msg) {
        try {
            Logger.info(String.format("onMessage: bot: %s from: %s",
                    client.getBotId(),
                    msg.getUserId()));

            // send echo back to user
            if (msg.getContent() != null) {
                client.sendText("You wrote: " + msg.getContent());
            }

            Message.ImageData imageData = msg.getImageData();
            if (imageData != null) {
                Logger.info(String.format("Received an Image\nname: %s\ntype: %s\nsize: %,d KB\nh: %d\nw: %d\ntag: %s",
                        msg.getName(),
                        msg.getMimeType(),
                        msg.getSize() / 1024,
                        imageData.getHeight(),
                        imageData.getWidth(),
                        imageData.getTag()
                ));

                // echo this image back to user
                byte[] img = client.downloadAsset(msg);
                
                // call image recognition API
                ImageClassificationRequest request = new ImageClassificationRequest();
                request.setImage(img);
                
                try {
                    ImageClassificationResponse response = recognition.doPost(request);
                    client.sendText(response.getClassification());
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.error(e.getMessage());
                    client.sendText("Sorry something went wrong :( ");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    /**
     * @param newBot NewBot object containing info about the conversation this bot is being added to
     *               This method is called when the User adds this bot into existing conversation.
     * @return True if this user is entitled to create new conversation with this bot
     */
    @Override
    public boolean onNewBot(NewBot newBot) {
        Logger.info(String.format("onNewBot: user: %s/%s, locale: %s",
                newBot.origin.id,
                newBot.origin.name,
                newBot.locale));

        // return false in case you don't want to allow this user to open new conv with your bot
        return true;
    }

    /**
     * This method is called when bot is added into new conversation and it's ready to posts into it.
     *
     * @param client BotClient object that can be used to post new content into this conversation
     */
    @Override
    public void onNewConversation(BotClient client) {
        try {
            Conversation conversation = client.getConversation();

            Logger.info(String.format("onNewConversation: bot: %s, conv: %s, name: %s",
                    client.getBotId(),
                    conversation.id,
                    conversation.name));

            client.sendText("Hello, send me some pictures to recognize objects for you :) ");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }


    /**
     * This method is called when new participant joins the conversation
     *
     * @param client  BotClient object that can be used to post new content into this conversation
     * @param userIds List of New participants that were just added into the conv.
     */
    @Override
    public void onMemberJoin(BotClient client, ArrayList<String> userIds) {
        try {
            Collection<User> users = client.getUsers(userIds);
            for (User user : users) {
                Logger.info(String.format("onMemberJoin: user: %s/%s, bot: %s",
                        user.id,
                        user.name,
                        client.getBotId()));

                // say Hi to new participant
                client.sendText("Hi there " + user.name + " send me some pictures :) ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    /**
     * This method is called when somebody leaves the conversation
     *
     * @param client  BotClient object that can be used to post new content into this conversation
     * @param userIds List of participants that just left the conv (or being kicked out of it :-p).
     */
    @Override
    public void onMemberLeave(BotClient client, ArrayList<String> userIds) {

    }

    /**
     * Overrides default bot name.
     *
     * @return Bot name
     */
    @Override
    public String getName() {
        return config.getName();
    }

    /**
     * Overrides default bot's accent colour.
     *
     * @return accent colour id [0-7]
     */
    @Override
    public int getAccentColour() {
        return config.getAccent();
    }

    /**
     * @return Array of picture keys
     */
    @Override
    public String[] getProfilePictures() {
        return config.getProfiles();
    }

}
