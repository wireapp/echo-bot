package com.wire.bots.github.utils;

import java.security.SecureRandom;
import java.math.BigInteger;

public final class SessionIdentifierGenerator {
    private final SecureRandom random = new SecureRandom();

    public String next() {
        return new BigInteger(130, random).toString(32);
    }

    public String next(int length) {
        return next().substring(0, length);
    }
}