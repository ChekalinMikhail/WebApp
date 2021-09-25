package org.example.app.util;

import java.security.SecureRandom;

public class PasswordRecoveryKeyGenerator {
    private PasswordRecoveryKeyGenerator() {
    }

    public static String generate() {
        final var secureRandom = new SecureRandom();
        StringBuilder key = new StringBuilder("");
        final var numbers = new int[6];
        for (int i = 0; i < numbers.length; i++) {
            key.append(secureRandom.nextInt(9));
        }
        return key.toString();
    }
}
