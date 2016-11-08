package com.wire.bots.hello;

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
    public String getName() {
        return config.getName();
    }

    @Override
    public int getAccentColour() {
        return config.getAccent();
    }

    @Override
    public String[] getProfilePictures() {
        return config.getProfiles();
    }

    @Override
    public boolean onNewBot(NewBot newBot) {
        Logger.info(String.format("onNewBot: user: %s/%s, locale: %s",
                newBot.origin.id,
                newBot.origin.name,
                newBot.locale));

        return true;
    }

    @Override
    public void onNewConversation(BotClient client) {
        try {
            Conversation conversation = client.getConversation();

            Logger.info(String.format("onNewConversation: bot: %s, conv: %s, name: %s",
                    client.getBotId(),
                    conversation.id,
                    conversation.name));

            client.sendText("Hello");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onMessage(BotClient client, Message msg) {
        try {
            Logger.info(String.format("onMessage: %s, bot: %s from: %s",
                    msg.getMessageType(),
                    client.getBotId(),
                    msg.getUserId()));

            // send echo back to user
            if (msg.getContent() != null)
                client.sendText("You wrote: " + msg.getContent());

            // send user some nice picture
            client.sendPicture("http://i.imgur.com/eGzcZih.png");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

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
                client.sendText("Hi there " + user.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onMemberLeave(BotClient client, ArrayList<String> userIds) {

    }
}
