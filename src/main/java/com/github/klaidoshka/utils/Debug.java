package com.github.klaidoshka.utils;

import java.util.Arrays;

public class Debug {

    public static void logAroundLines(String message) {
        System.out.println(System.lineSeparator() + message + System.lineSeparator());
    }

    public static void logAroundLines(String message, String line) {
        System.out.println(line + System.lineSeparator() + message + System.lineSeparator() + line);
    }

    public static void log(String... messages) {
        if (messages.length == 0) System.out.println();
        else Arrays.stream(messages).forEach(System.out::println);
    }
}
