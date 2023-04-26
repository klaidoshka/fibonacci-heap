package com.github.klaidoshka.structures.interfaces;

public interface IHeap<E> {

    /**
     * Clears heap of the accumulated data
     * <p>
     * Asymptotic time complexity = O(1)
     */
    void clear();

    /**
     * Extracts provided node from the heap by modifying its key to
     * the greatest priority in the heap and then extracting it
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @param node Node to extract from the heap
     * @return Extracted node
     */
    INode<E> extract(INode<E> node);

    /**
     * Extracts the prioritized (first) node in the heap
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @return Extracted node
     */
    INode<E> extractRoot();

    /**
     * Displays whole heap in a tree-like structure
     */
    void display();

    /**
     * Gets the prioritized node in the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Prioritized (first) heap node
     */
    INode<E> getRoot();

    /**
     * Inserts an element into the heap with its provided key
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param e Element to insert
     * @return Element's created Node
     */
    INode<E> insert(E e);

    /**
     * Checks if heap is empty
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return True if is empty, otherwise false
     */
    boolean isEmpty();

    /**
     * Merges provided heap into the current heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param heap Heap to use for the merge
     */
    void merge(IHeap<E> heap);

    /**
     * Modifies element of certain Node in the heap
     * <br>
     * <b>Element must not be greater than the previous one as per comparator's logic.</b>
     * <p>
     * Asymptotic time complexity = O(1) - amortized
     *
     * @param node    Node to decrease element for
     * @param element Element
     */
    void modifyElement(INode<E> node, E element);

    /**
     * Gets the amount of roots inside the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Amount of roots
     */
    int roots();

    /**
     * Gets the size of elements inside the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Size of heap elements
     */
    int size();
}
