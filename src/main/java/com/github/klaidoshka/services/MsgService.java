package com.github.klaidoshka.services;

import java.util.Arrays;

public class MsgService {

    public static void logAroundLines(String message) {
        System.out.println(System.lineSeparator() + message + System.lineSeparator());
    }

    public static void log(String... messages) {
        if (messages.length == 0) {
            System.out.println();
        } else {
            Arrays.stream(messages).forEach(System.out::println);
        }
    }
}
