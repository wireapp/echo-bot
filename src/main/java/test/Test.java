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
    Search for service called: Echo
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
        String userId = ep.signIn(email, password);

        Logger.info("Logged in as: %s, id: %s", email, userId);

        String keyword = "echo";
        String tags = "tutorial";

        Logger.info("Searching service: keyword: %s, tags: %s", keyword, tags);
        SearchClient searchClient = new SearchClient(ep.getToken());
        SearchClient.Result res = searchClient.search(tags, keyword);
        if (res.services.isEmpty()) {
            System.console().printf("Cannot find any service starting in: %s", keyword);
            return;
        }

        // Listen on incoming messages coming from other users
        MessageResource msgRes = new MessageResource(new MessageHandlerBase() {
            @Override
            public void onText(WireClient client, TextMessage msg) {
                Logger.info("Received: '%s' from: %s", msg.getText(), msg.getUserId());
            }
        }, config, repo);
        Session session = ep.connectWebSocket(msgRes);

        SearchClient.Service echo = res.services.get(0);
        Logger.info("Found service:\nname: %s\nsummary: %s\nserviceId: %s\nproviderId: %s",
                echo.name,
                echo.summary,
                echo.serviceId,
                echo.providerId);

        API api = new API(ep.getToken());

        Logger.info("Creating new conversation...");
        Conversation conversation = api.createConversation(echo.name);

        Logger.info("Adding service `%s` to conversation: `%s`", echo.name, conversation.name);
        User bot = api.addService(echo.serviceId, echo.providerId);
        Logger.info("New Bot `%s`, id:: %s", bot.name, bot.id);

        Thread.sleep(2000);

        Logger.info("Posting text into conversation: %s", conversation.name);
        WireClient wireClient = repo.getWireClient(userId, conversation.id);

        wireClient.sendText("Privet! Kak dela?");
        Thread.sleep(2000);

        session.close();
    }
}
