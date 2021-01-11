package com.wire.bots.echo;

import com.wire.blender.BlenderListener;
import com.wire.lithium.ClientRepo;
import com.wire.xenon.WireClient;
import com.wire.xenon.assets.Calling;
import com.wire.xenon.tools.Logger;

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
                client.send(new Calling(content));
            } catch (Exception e) {
                Logger.error("onCallingMessage: %s", e);
            }
        });
    }
}
