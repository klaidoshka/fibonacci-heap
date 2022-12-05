package com.github.klaidoshka.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private final static int leftLimit = 97; // randomized id left bound - letter 'a'
    private final static int rightLimit = 122; // randomized id right bound - letter 'z'
    private final static int length = 24; // randomized id length

    public static String randomizeAlphabeticString() {
        return ThreadLocalRandom.current()
                .ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomizeAlphabeticString(int length) {
        return ThreadLocalRandom.current()
                .ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static int randomizeNumber() {
        return ThreadLocalRandom.current()
                .nextInt(-(int) Math.pow(10, length), (int) Math.pow(10, length));
    }

    public static int randomizeNumber(int length) {
        return ThreadLocalRandom.current()
                .nextInt(-(int) Math.pow(10, length), (int) Math.pow(10, length));
    }
}
