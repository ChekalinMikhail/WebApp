package org.example.app.util;

import java.security.SecureRandom;

public class CardNumberGenerator {
    private CardNumberGenerator() {
    }

    public static String generate() {
        final var secureRandom = new SecureRandom();
        StringBuilder number = new StringBuilder("**** ");
        for (int i = 0; i < 4; i++) {
            number.append(secureRandom.nextInt(9));
        }
        return number.toString();
    }
}
