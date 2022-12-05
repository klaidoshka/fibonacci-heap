package com.github.klaidoshka;

import com.github.klaidoshka.structures.FibonacciHeap;
import com.github.klaidoshka.utils.Debug;
import com.github.klaidoshka.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        Debug.logAroundLines("PROGRAM HAS BEEN STARTED");

//        final int[] array = generateArray(10, 5);
//        final FibonacciHeap<Integer> heap = FibonacciHeap.init();
//        Arrays.stream(array).forEach(heap::insert);

//        insertManipulation(); // insert test +
        deleteMinManipulation(); // delete min test +
//        customDeleteManipulation(); // custom delete test +
//        mergeManipulation(); // merge test +
//        decreaseElementManipulation(); // decrease element test +

        Debug.logAroundLines("PROGRAM HAS BEEN STOPPED");
    }

    private static int[] generateArray(int arrayLength, int numbersLength) {
        return IntStream
                .range(0, arrayLength)
                .map(number -> RandomUtil.randomizeNumber(numbersLength))
                .toArray();
    }

    private static void insertManipulation() {
        Debug.log("* Insert Manipulation *", "");

        FibonacciHeap<Integer> heap = FibonacciHeap.init();

        heap.display(); // display before everything

        Debug.log();

        Arrays.stream(generateArray(10, 3))
                .forEach(heap::insert); // insert

        heap.display(); // display after insertion
    }

    private static void deleteMinManipulation() {
        Debug.log("* Delete Min Manipulation *", "");

        FibonacciHeap<Integer> heap = FibonacciHeap.init();

        Arrays.stream(generateArray(7, 3))
                .forEach(heap::insert); // insert

        heap.display(); // display after insertion
        Debug.log();

        heap.deleteMin();
        heap.display(); // display after deletion
        Debug.log();

        heap.deleteMin();
        heap.display(); // display after deletion
        Debug.log();

        heap.deleteMin();
        heap.display(); // display after deletion
    }

    private static void customDeleteManipulation() {
        Debug.log("* Custom Delete Manipulation *", "");

        FibonacciHeap<Integer> heap = FibonacciHeap.init();
        List<FibonacciHeap.Node<Integer>> nodes = new ArrayList<>();

        Arrays.stream(generateArray(8, 3))
                .forEach($ -> nodes.add(heap.insert($))); // some random inserts

        heap.display(); // display after insertion
        Debug.log();

        deleteNode(heap, nodes, 7);
        deleteNode(heap, nodes, 3);
        deleteNode(heap, nodes, 1);
        deleteNode(heap, nodes, 0);
    }

    private static void deleteNode(FibonacciHeap<Integer> heap, List<FibonacciHeap.Node<Integer>> nodes, int index) {
        Debug.log(" Deleting " + nodes.get(index) + "...", "");
        heap.delete(nodes.get(index));
        heap.display();
        Debug.log();
    }

    private static void mergeManipulation() {
        Debug.log("* Merge Manipulation *", "");

        FibonacciHeap<Integer> firstHeap = FibonacciHeap.init();
        FibonacciHeap<Integer> secondHeap = FibonacciHeap.init();

        Arrays.stream(generateArray(3, 3))
                .forEach(firstHeap::insert); // insert elements into first heap
        firstHeap.display(); // display after insertion
        Debug.log();

        Arrays.stream(generateArray(7, 5))
                .forEach(secondHeap::insert); // insert elements into second heap
        secondHeap.display(); // display after insertion
        Debug.log();

        firstHeap.merge(secondHeap);
        Debug.log("Roots : " + firstHeap.roots());
        firstHeap.display(); // display after merge
    }

    private static void decreaseElementManipulation() {
        Debug.log("* Decrease Element Manipulation *", "");

        FibonacciHeap<Integer> heap = FibonacciHeap.init();
        List<FibonacciHeap.Node<Integer>> nodes = new ArrayList<>();

        Arrays.stream(generateArray(10, 3))
                .forEach($ -> nodes.add(heap.insert($))); // some random inserts

        heap.display(); // display after insertion
        Debug.log();

        decreaseElement(heap, nodes, 0, 10);
        decreaseElement(heap, nodes, 4, -7);
        decreaseElement(heap, nodes, 7, 99);
        decreaseElement(heap, nodes, 2, -135701);
    }

    private static void decreaseElement(FibonacciHeap<Integer> heap, List<FibonacciHeap.Node<Integer>> nodes, int index, int value) {
        if (nodes.get(index).getElement() < value) {
            Debug.log("  -> Change didn't happen, because new value is greater than last one.", "");
            return;
        }
        Debug.log("  -> Changing " + nodes.get(index) + " value into " + value);
        heap.decreaseElement(nodes.get(index), value);
        heap.display(); // display after decrease
        Debug.log();
    }
}