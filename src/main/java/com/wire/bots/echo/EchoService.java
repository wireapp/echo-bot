//
// Wire
// Copyright (C) 2016 Wire Swiss GmbH
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.
//

package com.wire.bots.echo;

import com.google.common.collect.ImmutableMultimap;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.Server;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;

import java.io.PrintWriter;

public class EchoService extends Server<EchoConfig> {
    public static void main(String[] args) throws Exception {
        new EchoService().run(args);
    }

    @Override
    protected MessageHandlerBase createHandler(EchoConfig config, Environment env) {
        return new MessageHandler(config, env);
    }

    @Override
    protected void onRun(EchoConfig echoConfig, Environment env) {
        addTask(new Task("hello_task") {
            @Override
            public void execute(ImmutableMultimap<String, String> stringStringImmutableMultimap, PrintWriter printWriter) throws Exception {
                printWriter.println("This is hello task!");
                Logger.info("Executed Hello task");
            }
        }, env);
    }
}
