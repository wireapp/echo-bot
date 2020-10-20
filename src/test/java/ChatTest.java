import com.wire.bots.echo.Config;
import com.wire.bots.echo.Service;
import com.wire.bots.sdk.server.model.Conversation;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.user.API;
import com.wire.bots.sdk.user.LoginClient;
import com.wire.bots.sdk.user.model.Access;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class ChatTest {
    @ClassRule
    public static final DropwizardAppRule<Config> role = new DropwizardAppRule<>(Service.class,
            "echo.yaml",
            ConfigOverride.config("token", "dummy"));

    @Test
    public void addServiceTest() throws Exception {
        final Config config = role.getConfiguration();

        final String email = config.userMode.email;
        final String password = config.userMode.password;

        Service service = role.getApplication();

        Client client = service.getClient();

        LoginClient loginClient = new LoginClient(client);
        Access access = loginClient.login(email, password);

        UUID userId = access.getUserId();
        String token = access.getToken();

        API api = new API(client, null, token);

        UUID local = UUID.fromString("20be83de-f794-4a76-a4a7-593f8cf82f24");
        UUID prod = UUID.fromString("915e491e-e0c2-49cf-a678-4461c174c0e7");

        List<UUID> participants = Collections.singletonList(local);

        final Conversation conversation = api.createConversation("Test conv", null, participants);

        Logger.info("New conversation: %s with %s", conversation.id, participants.get(0));


    }
}
