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

import com.waz.model.Messages;
import com.wire.xenon.assets.IAsset;
import com.wire.xenon.assets.IGeneric;
import com.wire.xenon.tools.Util;

import java.security.SecureRandom;
import java.util.UUID;

public class SneakyPeek implements IGeneric, IAsset {
    static private final SecureRandom random = new SecureRandom();

    private final UUID messageId;
    private final byte[] encrypt;

    public SneakyPeek(UUID messageId) throws Exception {
        this.messageId = messageId;
        this.encrypt = encrypt(newOtrKey(), newOtrKey());
    }

    private static byte[] newOtrKey() {
        byte[] otrKey = new byte[32];
        random.nextBytes(otrKey);
        return otrKey;
    }

    @Override
    public Messages.GenericMessage createGenericMsg() {
        Messages.Asset.Builder asset = Messages.Asset.newBuilder()
                .setExpectsReadConfirmation(true)
                .setNotUploaded(Messages.Asset.NotUploaded.CANCELLED);

        return Messages.GenericMessage.newBuilder()
                .setMessageId(getMessageId().toString())
                .setAsset(asset)
                .build();
    }

    @Override
    public String getMimeType() {
        return "image/jpeg";
    }

    @Override
    public String getRetention() {
        return "expiring";
    }

    @Override
    public byte[] getEncryptedData() {
        return encrypt;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    private byte[] encrypt(byte[] bytes, byte[] otrKey) throws Exception {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return Util.encrypt(otrKey, bytes, iv);
    }
}
