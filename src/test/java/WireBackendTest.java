import com.waz.model.Messages;
import com.wire.bots.cryptobox.CryptoException;
import com.wire.bots.echo.Config;
import com.wire.bots.echo.Service;
import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.factories.CryptoFactory;
import com.wire.bots.sdk.models.otr.PreKeys;
import com.wire.bots.sdk.models.otr.Recipients;
import com.wire.bots.sdk.server.model.*;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WireBackendTest {
    private static final String serviceAuth = "secret";
    private static final String BOT_CLIENT_DUMMY = "bot_client_dummy";
    private static final String USER_CLIENT_DUMMY = "user_client_dummy";
    private static final DropwizardTestSupport<Config> SUPPORT = new DropwizardTestSupport<>(
            Service.class, "echo.yaml",
            ConfigOverride.config("token", serviceAuth));
    private Client client;
    private CryptoFactory cryptoFactory;

    @Before
    public void beforeClass() throws Exception {
        SUPPORT.before();
        Service app = SUPPORT.getApplication();
        client = app.getClient();
        cryptoFactory = app.getCryptoFactory();
    }

    @After
    public void afterClass() {
        SUPPORT.after();
    }

    @Test
    public void incomingMessageFromBackendTest() throws CryptoException, IOException {
        final UUID botId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID convId = UUID.randomUUID();

        // Test Bot added into conv. BE calls POST /bots with NewBot object
        NewBotResponseModel newBotResponseModel = newBotFromBE(botId, userId, convId);
        assertThat(newBotResponseModel.lastPreKey).isNotNull();
        assertThat(newBotResponseModel.preKeys).isNotNull();

        final Crypto crypto = cryptoFactory.create(botId);
        PreKeys preKeys = new PreKeys(newBotResponseModel.preKeys, USER_CLIENT_DUMMY, userId);

        // Test Ping message is sent to Echo by the BE. BE calls POST /bots/{botId}/messages with Payload obj
        Recipients encrypt = crypto.encrypt(preKeys, generatePingMessage());
        String cypher = encrypt.get(userId, USER_CLIENT_DUMMY);
        Response res = newOtrMessageFromBackend(botId, userId, cypher);
        assertThat(res.getStatus()).isEqualTo(200);

        crypto.close();
    }

    private NewBotResponseModel newBotFromBE(UUID botId, UUID userId, UUID convId) {
        NewBot newBot = new NewBot();
        newBot.id = botId;
        newBot.locale = "en";
        newBot.token = "token_dummy";
        newBot.client = BOT_CLIENT_DUMMY;
        newBot.origin = new User();
        newBot.origin.id = userId;
        newBot.origin.name = "user_name";
        newBot.origin.handle = "user_handle";
        newBot.conversation = new Conversation();
        newBot.conversation.id = convId;
        newBot.conversation.name = "conv_name";
        newBot.conversation.creator = userId;
        newBot.conversation.members = new ArrayList<>();

        Response res = client
                .target("http://localhost:" + SUPPORT.getLocalPort())
                .path("bots")
                .request()
                .header("Authorization", "Bearer " + serviceAuth)
                .post(Entity.entity(newBot, MediaType.APPLICATION_JSON_TYPE));

        assertThat(res.getStatus()).isEqualTo(201);

        return res.readEntity(NewBotResponseModel.class);
    }

    private Response newOtrMessageFromBackend(UUID botId, UUID userId, String cypher) {
        Payload payload = new Payload();
        payload.type = "conversation.otr-message-add";
        payload.from = userId;
        payload.time = new Date().toString();
        payload.data = new Payload.Data();
        payload.data.sender = USER_CLIENT_DUMMY;
        payload.data.recipient = BOT_CLIENT_DUMMY;
        payload.data.text = cypher;

        return client
                .target("http://localhost:" + SUPPORT.getLocalPort())
                .path("bots")
                .path(botId.toString())
                .path("messages")
                .request()
                .header("Authorization", "Bearer " + serviceAuth)
                .post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE));
    }

    private byte[] generatePingMessage() {
        return Messages.GenericMessage.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setKnock(Messages.Knock.newBuilder().setHotKnock(false))
                .build()
                .toByteArray();
    }
}