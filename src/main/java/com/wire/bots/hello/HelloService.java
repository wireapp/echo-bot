package com.wire.bots.hello;

import com.wire.wbotz.MessageHandlerBase;
import com.wire.wbotz.Server;

public class HelloService extends Server<HelloConfig> {
    public static void main(String[] args) throws Exception {
        new HelloService().run(args);
    }

    @Override
    protected MessageHandlerBase createHandler(HelloConfig config) {
        return new MessageHandler(config);
    }

    @Override
    protected void onRun(HelloConfig config) {
    }
}
