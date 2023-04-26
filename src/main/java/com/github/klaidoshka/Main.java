package com.github.klaidoshka;

import com.github.klaidoshka.services.MsgService;
import com.github.klaidoshka.structures.FibonacciHeap;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        final FibonacciHeap<String> heapFirst = FibonacciHeap.create(Comparator.<String>naturalOrder());

        heapFirst.insert("First element has been successfully added.");
        heapFirst.insert("Second element has been successfully added.");
        heapFirst.insert("Third element has been successfully added.");

        MsgService.logAroundLines("First heap filled...");

        heapFirst.display();

        final FibonacciHeap<String> heapSecond = FibonacciHeap.create(Comparator.<String>reverseOrder());

        heapSecond.insert("Fourth element has been successfully added.");
        heapSecond.insert("Fifth element has been successfully added.");
        heapSecond.insert("Sixth element has been successfully added.");

        MsgService.logAroundLines("Second heap filled...");

        heapSecond.display();

        heapFirst.merge(heapSecond);

        MsgService.logAroundLines("Merged heaps...");

        heapFirst.display();
    }
}