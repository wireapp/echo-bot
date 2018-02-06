package test;

import com.wire.bots.sdk.Configuration;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.crypto.CryptoFile;
import com.wire.bots.sdk.factories.CryptoFactory;
import com.wire.bots.sdk.factories.StorageFactory;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.server.model.User;
import com.wire.bots.sdk.storage.FileStorage;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;
import com.wire.bots.sdk.user.API;
import com.wire.bots.sdk.user.Endpoint;
import com.wire.bots.sdk.user.UserClientRepo;
import com.wire.bots.sdk.user.UserMessageResource;

import javax.websocket.Session;

/*
    Sign using Wire credentials (email/password).
    Search for service named: args[0]
    Create new conversation and add this service
    Send some text in this conversation
*/
public class Test {
    private static final String CRYPTO_DIR = "./data";

    public static void main(String[] args) throws Exception {

        String email = System.getProperty("email");
        String password = System.getProperty("password");

        Configuration config = new Configuration();
        config.data = CRYPTO_DIR;

        Endpoint ep = new Endpoint(config);
        String userId = ep.signIn(email, password, false);

        Logger.info("Logged in as: %s, id: %s, domain: %s", email, userId, Util.getDomain());

        StorageFactory storageFactory = botId -> new FileStorage(CRYPTO_DIR, botId);
        CryptoFactory cryptoFactory = botId -> new CryptoFile(CRYPTO_DIR, botId);

        UserClientRepo repo = new UserClientRepo(cryptoFactory, storageFactory);

        // Listen on incoming messages coming from other users
        UserMessageResource msgRes = new UserMessageResource(new MessageHandlerBase() {
            @Override
            public void onText(WireClient client, TextMessage msg) {
                Logger.info("Received: '%s' from: %s", msg.getText(), msg.getUserId());
            }
        }, repo);

        Session session = ep.connectWebSocket(msgRes);

        String keyword = args.length == 0 ? "" : args[0];
        String tags = "integration";

        Logger.info("Searching services starting in: `%s`, tags: %s", keyword, tags);
        SearchClient searchClient = new SearchClient(ep.getToken());
        SearchClient.Result res = searchClient.search(tags, keyword);
        if (res.services.isEmpty()) {
            Logger.info("Cannot find any service starting in: `%s`\n", keyword);
            return;
        }

        SearchClient.Service service = res.services.get(0);

        Logger.info("Found service:\nname: %s\nsummary: %s\nserviceId: %s\nproviderId: %s",
                service.name,
                service.summary,
                service.serviceId,
                service.providerId);


        // Create new conversation in which we are going to talk to
        Logger.info("Creating new conversation...");
        Conversation conversation = API.createConversation(service.name, ep.getToken());

        API api = new API(conversation.id, ep.getToken());

        try {
            // Add this service (Bot) into this conversation
            Logger.info("Adding service `%s` to conversation: `%s`", service.name, conversation.name);
            User bot = api.addService(service.serviceId, service.providerId);
            Logger.info("New Bot `%s`, id:: %s", bot.name, bot.id);
            Thread.sleep(2000);

            // Post some text into this conversation
            String txt = "Privet! Kak dela?";
            Logger.info("Posting text: `%s`", txt);
            WireClient wireClient = repo.getWireClient(userId, conversation.id);
            wireClient.sendText(txt);
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        } finally {
            Logger.info("Deleting conversation: %s", conversation.name);
            String teamId = "a31fd99e-0b0f-46cd-b3fe-e01b4691c8dc";
            api.deleteConversation(teamId);
        }

        Thread.sleep(2000);

        session.close();
    }
}
