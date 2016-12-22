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

package com.wire.bots.hello;

import com.wire.wbotz.MessageHandlerBase;
import com.wire.wbotz.Server;
import io.dropwizard.setup.Environment;

public class HelloService extends Server<HelloConfig> {
    public static void main(String[] args) throws Exception {
        new HelloService().run(args);
    }

    @Override
    protected MessageHandlerBase createHandler(HelloConfig config) {
        return new MessageHandler(config);
    }

    @Override
    protected void onRun(HelloConfig helloConfig, Environment environment) {

    }
}
