package com.wire.bots.echo;

import com.wire.blender.BlenderListener;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.tools.Logger;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CallListener implements BlenderListener {
    private final ClientRepo repo;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

    CallListener(ClientRepo repo) {
        this.repo = repo;
    }

    @Override
    public void onCallingMessage(String id,
                                 String userId,
                                 String clientId,
                                 String peerId,
                                 String peerClientId,
                                 String content,
                                 boolean trans) {

        Logger.info("id: %s, user: (%s-%s), peer: (%s-%s), content: %s, transient: %s",
                id,
                userId,
                clientId,
                peerId,
                peerClientId,
                content,
                trans);

        executor.execute(() -> {
            try (WireClient client = repo.getClient(UUID.fromString(id))) {
                client.call(content);
            } catch (Exception e) {
                Logger.error("onCallingMessage: %s", e);
            }
        });
    }
}
