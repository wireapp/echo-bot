import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.wire.bots.echo.Config;
import com.wire.bots.echo.Service;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.crypto.CryptoDatabase;
import com.wire.bots.sdk.crypto.storage.RedisStorage;
import com.wire.bots.sdk.factories.CryptoFactory;
import com.wire.bots.sdk.factories.StorageFactory;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.server.model.User;
import com.wire.bots.sdk.state.RedisState;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;
import com.wire.bots.sdk.user.API;
import com.wire.bots.sdk.user.LoginClient;
import com.wire.bots.sdk.user.UserClientRepo;
import com.wire.bots.sdk.user.model.Access;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
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
public class SearchTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(Service.class, "echo.yaml");
    private static Client client;

    private static void connectService(UUID teamId, SearchClient.Service service, String token, int count) {
        try {
            API api = new API(client, null, token);
            Conversation conv = api.createConversation(service.name, teamId, null, token);
            api = new API(client, conv.id, token);
            User bot = api.addService(service.serviceId, service.providerId);
            Logger.info("%,d. New Bot  `%s`, id:: %s", count, bot.name, bot.id);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    private static SearchClient.Service search(String keyword, String tags, String token)
            throws IOException {
        SearchClient searchClient = new SearchClient(client, token);
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

    private static SearchClient.Service search(UUID teamId, String keyword, String token)
            throws IOException {
        SearchClient searchClient = new SearchClient(client, token);
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

    @Test
    public void addServiceTest() throws Exception {
        final String email = System.getProperty("email");
        final String password = System.getProperty("password");
        final String keyword = System.getProperty("keyword");

        final Config config = app.getConfiguration();
        
        client = new JerseyClientBuilder(app.getEnvironment())
                .using(config.jerseyClient)
                .withProvider(MultiPartFeature.class)
                .withProvider(JacksonJsonProvider.class)
                .build("Test");

        LoginClient loginClient = new LoginClient(client);
        final Access access = loginClient.login(email, password);

        final String token = access.getToken();
        final UUID userId = access.getUserId();

        API api = new API(client, null, token);
        final UUID teamId = api.getTeam();

        Logger.info("Logged in as: %s, id: %s, domain: %s", email, userId, Util.getDomain());

        RedisStorage storage = new RedisStorage(config.db.host, config.db.port, config.db.password);
        StorageFactory storageFactory = botId -> new RedisState(botId, config.db);
        CryptoFactory cryptoFactory = botId -> new CryptoDatabase(botId, storage);

        UserClientRepo repo = new UserClientRepo(client, cryptoFactory, storageFactory);

        SearchClient.Service service;

        if (System.getProperty("service") != null && System.getProperty("provider") != null) {
            UUID serviceId = UUID.fromString(System.getProperty("service"));
            UUID providerId = UUID.fromString(System.getProperty("provider"));

            service = new SearchClient.Service();
            service.name = "Wire Bots";
            service.providerId = serviceId;
            service.serviceId = providerId;
        } else {
            String tags = "integration";
            Logger.info("Searching services starting in: `%s`, tags: %s", keyword, tags);

            service = search(teamId, keyword, token);
            if (service == null) {
                Logger.info("Cannot find any service starting in: `%s`\n", keyword);
                return;
            }
        }

        // Create new conversation in which we are going to talk to
        Logger.info("Creating new conversation...");
        final Conversation conversation = api.createConversation(service.name, teamId, null, token);

        api = new API(client, conversation.id, token);

        final String convName = conversation.name;
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
            if (api.deleteConversation(teamId)) {
                Logger.info("Deleted conversation: %s", convName);
            }
        }

        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(12);
        for (int i = 0; i < 0; i++) {
            final int count = i;
            executor.execute(() -> {
                connectService(teamId, service, token, count);
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        loginClient.logout(access.getToken(), access.getCookie());
    }
}
