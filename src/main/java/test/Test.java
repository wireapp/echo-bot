package test;

import com.wire.bots.sdk.*;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.server.model.User;
import com.wire.bots.sdk.server.resources.MessageResource;
import com.wire.bots.sdk.user.API;
import com.wire.bots.sdk.user.Endpoint;
import com.wire.bots.sdk.user.UserClient;

import javax.websocket.Session;

/*
    Sign using Wire credentials (email/password).
    Search for service named: args[0]
    Create new conversation and add this service
    Send some text in this conversation
*/
public class Test {
    private static final String CRYPTO_DIR = "data";

    public static void main(String[] args) throws Exception {

        String email = System.getProperty("email");
        String password = System.getProperty("password");

        Configuration config = new Configuration();
        config.cryptoDir = CRYPTO_DIR;

        WireClientFactory userClientFactory = (botId, convId, clientId, token) -> {
            String path = String.format("%s/%s", CRYPTO_DIR, botId);
            OtrManager otrManager = new OtrManager(path);
            return new UserClient(otrManager, botId, convId, clientId, token);
        };
        ClientRepo repo = new ClientRepo(userClientFactory, CRYPTO_DIR);


        Endpoint ep = new Endpoint(config);
        String userId = ep.signIn(email, password, false);

        Logger.info("Logged in as: %s, id: %s", email, userId);

        // Listen on incoming messages coming from other users
        MessageResource msgRes = new MessageResource(new MessageHandlerBase() {
            @Override
            public void onText(WireClient client, TextMessage msg) {
                Logger.info("Received: '%s' from: %s", msg.getText(), msg.getUserId());
            }
        }, config, repo);
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

        API api = new API(ep.getToken());

        // Create new conversation in which we are going to talk to
        Logger.info("Creating new conversation...");
        Conversation conversation = api.createConversation(service.name);

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

        } finally {
            //Logger.info("Deleting conversation: %s", conversation.id);
            //api.deleteConversation("a31fd99e-0b0f-46cd-b3fe-e01b4691c8dc");
        }

        Thread.sleep(2000);

        session.close();
    }
}
