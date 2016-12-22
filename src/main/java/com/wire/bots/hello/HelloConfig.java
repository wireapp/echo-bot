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

/**
 * Created with IntelliJ IDEA.
 * User: dejankovacevic
 * Date: 22/10/16
 * Time: 14:56
 */
public class HelloConfig extends com.wire.wbotz.Configuration {
    public String name;
    public int accent;
    private String smallProfile;
    private String bigProfile;

    public String getName() {
        return name;
    }

    public int getAccent() {
        return accent;
    }

    public String getSmallProfile() {
        return smallProfile;
    }

    public String getBigProfile() {
        return bigProfile;
    }
}
