package test;

import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.crypto.CryptoFile;
import com.wire.bots.sdk.factories.CryptoFactory;
import com.wire.bots.sdk.factories.StorageFactory;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.server.model.User;
import com.wire.bots.sdk.state.FileState;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;
import com.wire.bots.sdk.user.API;
import com.wire.bots.sdk.user.Endpoint;
import com.wire.bots.sdk.user.UserClientRepo;
import com.wire.bots.sdk.user.UserMessageResource;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/*
    Sign in using Wire credentials (email/password).
    Search for service named: args[0]
    Create new conversation and add this service
    Send some text in this conversation
*/
public class Test {
    private static final String CRYPTO_DIR = "./data";

    public static void main(String[] args) throws Exception {
        String email = System.getProperty("email");
        String password = System.getProperty("password");
        String keyword = System.getProperty("keyword");

        String serviceId = System.getProperty("service");
        String providerId = System.getProperty("provider");

        Endpoint ep = new Endpoint(CRYPTO_DIR);
        String userId = ep.signIn(email, password, false);
        String token = ep.getToken();
        String teamId = new API(null, token).getTeam();

        Logger.info("Logged in as: %s, id: %s, domain: %s", email, userId, Util.getDomain());

        StorageFactory storageFactory = botId -> new FileState(CRYPTO_DIR, botId);
        CryptoFactory cryptoFactory = botId -> new CryptoFile(CRYPTO_DIR, botId);

        UserClientRepo repo = new UserClientRepo(cryptoFactory, storageFactory);

        // Listen on incoming messages coming from other users
        UserMessageResource msgRes = new UserMessageResource(new MessageHandlerBase() {
            @Override
            public void onText(WireClient client, TextMessage msg) {
                Logger.info("Received: '%s' from: %s", msg.getText(), msg.getUserId());
            }
        }, repo);

        // ep.connectWebSocket(msgRes);

        SearchClient.Service service;

        if (serviceId != null && providerId != null) {
            service = new SearchClient.Service();
            service.name = "Wire Bots";
            service.providerId = serviceId;
            service.serviceId = providerId;
        } else {
            String tags = "integration";
            Logger.info("Searching services starting in: `%s`, tags: %s", keyword, tags);

            service = search(UUID.fromString(teamId), keyword, token);
            if (service == null) {
                Logger.info("Cannot find any service starting in: `%s`\n", keyword);
                return;
            }
        }

        // Create new conversation in which we are going to talk to
        Logger.info("Creating new conversation...");
        Conversation conversation = API.createConversation(service.name, token);

        API api = new API(conversation.id, token);

        String convName = conversation.name;
        try {
            // Add this service (Bot) into this conversation
            Logger.info("Adding service `%s` to conversation: `%s`", service.serviceId, convName);
            User bot = api.addService(service.serviceId, service.providerId);
            Logger.info("New Bot  `%s`, id:: %s", bot.name, bot.id);
            Thread.sleep(1000);

            // Post some text into this conversation
            String txt = "Hello";
            Logger.info("Posting text: `%s`", txt);
            WireClient wireClient = repo.getWireClient(userId, conversation.id);
            wireClient.sendText(txt);
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        } finally {
//            if (teamId != null && api.deleteConversation(teamId)) {
//                Logger.info("Deleted conversation: %s", convName);
//            }
        }

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(12);
        for (int i = 0; i < 0; i++) {
            final int count = i;
            executor.execute(() -> {
                connectService(service, token, count);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

    }

    private static void connectService(SearchClient.Service service, String token, int count) {
        try {
            Conversation conv = API.createConversation(service.name, token);
            API api = new API(conv.id, token);
            User bot = api.addService(service.serviceId, service.providerId);
            Logger.info("%,d. New Bot  `%s`, id:: %s", count, bot.name, bot.id);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    private static SearchClient.Service search(String keyword, String tags, String token) throws IOException {
        SearchClient searchClient = new SearchClient(token);
        SearchClient.Result res = searchClient.search(tags, keyword);
        if (!res.services.isEmpty()) {
            SearchClient.Service service = res.services.get(0);

            Logger.info("Found service:\nname: %s\nsummary: %s\nserviceId: %s\nproviderId: %s",
                    service.name,
                    service.summary,
                    service.serviceId,
                    service.providerId);
            return service;
        }
        return null;
    }

    private static SearchClient.Service search(UUID teamId, String keyword, String token) throws IOException {
        SearchClient searchClient = new SearchClient(token);
        SearchClient.Result res = searchClient.search(teamId, keyword);
        if (!res.services.isEmpty()) {
            SearchClient.Service service = res.services.get(0);

            Logger.info("Found service:\nname: %s\nsummary: %s\nserviceId: %s\nproviderId: %s",
                    service.name,
                    service.summary,
                    service.serviceId,
                    service.providerId);
            return service;
        }
        return null;
    }
}
