package com.github.klaidoshka.benchmarks;

import com.github.klaidoshka.structures.FibonacciHeap;
import com.github.klaidoshka.utils.RandomUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(time = 1)
@Measurement(time = 1)
public class BenchmarkJMH {

    @State(Scope.Benchmark)
    public static class HeapWrapper {

        private FibonacciHeap<Integer> fibonacciHeap;
        private List<FibonacciHeap.Node<Integer>> fibonacciNodes;

        @Setup(Level.Invocation)
        public void initHeap(BenchmarkParams params) {
            this.fibonacciHeap = FibonacciHeap.init();
            this.fibonacciNodes = new ArrayList<>();
            BenchmarkJMH.collectRandomizedIntegers(Integer.parseInt(params.getParam("elementsSize")))
                    .forEach($ -> fibonacciNodes.add(fibonacciHeap.insert($)));
        }
    }

    @Param({"10000", "20000", "40000", "80000", "160000"})
    private int elementsSize;

    private List<Integer> elements;

    @Setup(Level.Iteration)
    public void generateElements() {
        this.elements = collectRandomizedIntegers(elementsSize);
    }

    @Benchmark
    public void insert() {
        FibonacciHeap<Integer> fibonacciHeap = new FibonacciHeap<>();
        elements.forEach(fibonacciHeap::insert);
    }

    @Benchmark
    public void merge(HeapWrapper heapWrapper, HeapWrapper otherHeapWrapper) {
        heapWrapper.fibonacciHeap.merge(otherHeapWrapper.fibonacciHeap);
    }

    @Benchmark
    public void decreaseElement(HeapWrapper heapWrapper) {
        heapWrapper.fibonacciNodes.forEach($ -> heapWrapper.fibonacciHeap.decreaseElement($, $.getElement() - 1));
    }

    @Benchmark
    public void delete(HeapWrapper heapWrapper) {
        heapWrapper.fibonacciNodes.forEach(heapWrapper.fibonacciHeap::delete);
    }

    @Benchmark
    public void deleteMin(HeapWrapper heapWrapper) {
        for (int i = 0; i < elementsSize; i++)
            heapWrapper.fibonacciHeap.deleteMin();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkJMH.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    public static List<Integer> collectRandomizedIntegers(int elementsSize) {
        return IntStream.range(0, elementsSize)
                .mapToObj($ -> RandomUtil.randomizeNumber(5))
                .collect(Collectors.toList());
    }
}
