package com.github.klaidoshka.structures.interfaces;

public interface INode<E> {

    /**
     * Returns the element of the node
     *
     * @return Element
     */
    E getElement();

    /**
     * Transforms node into a string with its element and its weight.
     * If node is marked, then the string contains `*` symbol
     *
     * @return String representation of the node
     */
    String toString();
}
