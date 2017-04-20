package com.wire.bots.github.resource;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wire.bots.github.model.GitHubPullRequest;
import com.wire.bots.github.BotConfig;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.Util;
import com.wire.bots.sdk.WireClient;

@Path("/github")
public class GitHubResource {
    private final BotConfig conf;
    //private final Executor exec;
    private ClientRepo repo;

    public GitHubResource(ClientRepo repo, BotConfig conf) {
        this.repo = repo;
        this.conf = conf;
    }

    @POST
    @Path("/{botId}")
    public Response broadcast(@PathParam("botId") String botId,
                              @HeaderParam("X-GitHub-Event") String event,
                              @HeaderParam("X-Hub-Signature") String signature,
                              @HeaderParam("X-GitHub-Delivery") String delivery,
                              String payload) throws Exception {

        Logger.info("Event: %s, Signature: %s, Delivery: %s", event, signature, delivery);

        String secret = Util.readLine(new File(String.format("%s/%s/secret", conf.getCryptoDir(), botId)));
        String challenge = getSha(payload, secret);
        if (!challenge.equals(signature)) {
            Logger.warning("Invalid sha");
            return Response.
                    status(403).
                    build();
        }

        ObjectMapper mapper = new ObjectMapper();

        switch (event) {
            case "pull_request": {
                GitHubPullRequest gitHubPullRequest = mapper.readValue(payload, GitHubPullRequest.class);
                WireClient client = repo.getWireClient(botId);
                client.sendText(gitHubPullRequest.pr.url);
                break;
            }
        }

        return Response.
                ok().
                build();
    }

    private String getSha(String payload, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secret.getBytes(Charset.forName("UTF-8")), "HmacSHA1"));
        byte[] bytes = hmac.doFinal(payload.getBytes(Charset.forName("UTF-8")));
        return String.format("sha1=%040x", new BigInteger(1, bytes));
    }
}
