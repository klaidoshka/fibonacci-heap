package com.github.klaidoshka.structures;

import com.github.klaidoshka.services.MsgService;
import com.github.klaidoshka.structures.interfaces.IHeap;
import com.github.klaidoshka.structures.interfaces.INode;

import java.util.Comparator;

public class FibonacciHeap<E extends Comparable<E>> implements IHeap<E> {

    /**
     * Comparator for heap's entries arrangement
     */
    private final Comparator<E> comparator;
    /**
     * Smallest node inside the heap
     */
    private Node<E> min;
    /**
     * Roots amount inside the heap
     */
    private int roots;
    /**
     * Size (amount) of entries inside the heap
     */
    private int size;

    /**
     * Empty constructor which uses default comparator of natural order
     * for heap's entries
     */
    public FibonacciHeap() {
        comparator = Comparator.naturalOrder();
    }

    /**
     * Constructor that accepts custom comparator for heap's entries
     *
     * @param comparator Comparator to use for heap's entries
     */
    public FibonacciHeap(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Static constructor that creates a heap with default comparator of
     * natural order for its entries
     *
     * @param <E> Type of entries inside the heap
     * @return FibonacciHeap<E>
     */
    public static <E extends Comparable<E>> FibonacciHeap<E> create() {
        return new FibonacciHeap<>();
    }

    /**
     * Static constructor that creates a heap with custom comparator
     * for its entries
     *
     * @param comparator Comparator for heap's entries
     * @param <E>        Type of entries inside the heap
     * @return FibonacciHeap<E>
     */
    public static <E extends Comparable<E>> FibonacciHeap<E> create(Comparator<E> comparator) {
        return new FibonacciHeap<>(comparator);
    }

    /**
     * Recursive cutting of marked nodes from their parent nodes by
     * going all the way up
     * <br> <br>
     * <b>Node is marked when these 2 things happen:</b>
     * <br>
     * * Node is a child of X node
     * <br>
     * * Node's child has been cut (at least one)
     * <p>
     * Asymptotic time complexity = O(1) - amortized
     *
     * @param nodeToCutOut Node to start the cutting from
     */
    private void cascadingCut(Node<E> nodeToCutOut) {
        final Node<E> parent = nodeToCutOut.parent;

        if (parent == null) {
            return;
        }

        if (!nodeToCutOut.marked) {
            nodeToCutOut.marked = true;
        } else {
            cut(nodeToCutOut, parent);

            cascadingCut(parent);
        }
    }

    /**
     * Clears heap of the accumulated data
     * <p>
     * Asymptotic time complexity = O(1)
     */
    public void clear() {
        min = null;
        size = 0;
        roots = 0;
    }

    /**
     * Opposite of what {@link #link(Node, Node)} is doing.
     * Cuts provided node from provided parent's node.
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param nodeToCut  Node to cut from the parent
     * @param parentNode Parent that has the node
     */
    private void cut(Node<E> nodeToCut, Node<E> parentNode) {
        cut(nodeToCut, parentNode, true);
    }

    /**
     * Opposite of what {@link #link(Node, Node)} is doing.
     * Cuts provided node from provided parent's node.
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param nodeToCut  Node to cut from the parent
     * @param parentNode Parent that has the node
     * @param unmark     Whether to unmark the node
     */
    private void cut(Node<E> nodeToCut, Node<E> parentNode, boolean unmark) {
        removeSideLinks(nodeToCut);

        parentNode.weight--;

        if (parentNode.child == nodeToCut) {
            parentNode.child = nodeToCut != nodeToCut.right ? nodeToCut.right : null;
        }

        insertAfter(nodeToCut, min);

        roots++;
        nodeToCut.parent = null;

        if (unmark) {
            nodeToCut.marked = false;
        }
    }

    /**
     * Extracts provided node from the heap by modifying its key to
     * the greatest priority in the heap and then extracting it
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @param node Node to extract from the heap
     * @return Extracted node
     */
    public Node<E> extract(INode<E> node) {
        if (!(node instanceof Node<E> fibonacciNode)) {
            throw new Error("Provided node is not a FibonacciHeap.Node instance at FibonacciHeap#extract.");
        }

        rearrange(fibonacciNode, true);

        return extractRoot();
    }

    /**
     * Extracts the prioritized (first) node in the heap
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @return Extracted node
     */
    public Node<E> extractRoot() {
        if (min == null) {
            return null;
        }

        final Node<E> nodeToDelete = min;

        while (nodeToDelete.child != null) {
            cut(nodeToDelete.child, nodeToDelete, false);
        }

        removeSideLinks(nodeToDelete);

        if (nodeToDelete == nodeToDelete.right) {
            clear();
        } else {
            min = nodeToDelete.right;
            size--;
            roots--;

            restructure();
        }

        return nodeToDelete;
    }

    /**
     * Displays whole heap in a tree-like structure
     */
    public void display() {
        MsgService.log(" HEAP SHOWCASE * " + size + " Entries [ Minimum: " + (min == null ? "<?>" : min) + " ]");

        if (isEmpty()) {
            MsgService.log("  -> HEAP IS EMPTY");
        } else {
            display(min, " ");
        }
    }

    /**
     * Helper for the exposed display method
     */
    private void display(Node<E> node, String prefix) {
        if (node == null) {
            return;
        }

        Node<E> temp = node;
        Node<E> k;

        do {
            MsgService.log(prefix + " -> " + temp.toString());

            k = temp.child;

            display(k, prefix + "   ");

            temp = temp.right;
        } while (temp != node);
    }

    /**
     * Gets the prioritized node in the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Prioritized (first) heap node
     */
    public Node<E> getRoot() {
        return min;
    }

    /**
     * Inserts an element into the heap with its provided key
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param e Element to insert
     * @return Element's created Node
     */
    public Node<E> insert(E e) {
        final Node<E> node = new Node<>(e);

        size++;
        roots++;

        if (isEmpty()) {
            min = node;
        } else {
            insertAfter(node, min);

            if (comparator.compare(node.element, min.element) < 0) {
                min = node;
            }
        }

        return node;
    }

    /**
     * Checks if heap is empty
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return True if is empty, otherwise false
     */
    public boolean isEmpty() {
        return min == null;
    }

    /**
     * Links child to the parent's child list and sets it
     * as unmarked
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param child  Child to link
     * @param parent Parent to whom the child will be linked
     */
    private void link(Node<E> child, Node<E> parent) {
        removeSideLinks(child);

        child.parent = parent;

        if (parent.child == null) {
            parent.child = child;
            child.right = child;
            child.left = child;
        } else {
            child.left = parent.child;
            child.right = parent.child.right;
            parent.child.right = child;
            child.right.left = child;
        }

        parent.weight++;
        child.marked = false;
    }

    /**
     * Merges provided heap into the current heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param heap Heap to use for the merge
     */
    public void merge(IHeap<E> heap) {
        if (heap == null || heap.isEmpty()) {
            return;
        }

        if (!(heap instanceof FibonacciHeap<E> fibonacciHeap)) {
            throw new Error("Provided heap is not a FibonacciHeap at FibonacciHeap#merge.");
        }

        size += fibonacciHeap.size;
        roots += fibonacciHeap.roots;

        if (min == null) {
            min = fibonacciHeap.min;
        } else {
            final Node<E> rightNode = min.right;
            final Node<E> anotherRightNode = fibonacciHeap.min.right;

            min.right = anotherRightNode;
            anotherRightNode.left = min;
            rightNode.left = fibonacciHeap.min;
            fibonacciHeap.min.right = rightNode;

            if (comparator.compare(fibonacciHeap.min.element, min.element) < 0) {
                min = fibonacciHeap.min;
            }
        }
    }

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
    public void modifyElement(INode<E> node, E element) {
        if (element == null) {
            throw new Error("Element is null at FibonacciHeap#decreaseElement.");
        }

        if (!(node instanceof Node<E> fibonacciNode)) {
            throw new Error("Provided node is not a FibonacciHeap.Node instance at FibonacciHeap#modifyElement.");
        }

        if (comparator.compare(fibonacciNode.element, element) < 0) {
            throw new Error("Element is greater than previous one at FibonacciHeap#decreaseElement.");
        }

        fibonacciNode.element = element;

        rearrange(fibonacciNode, false);
    }

    /**
     * Gets the amount of roots inside the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Amount of roots
     */
    public int roots() {
        return roots;
    }

    /**
     * Gets the size of elements inside the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Size of heap elements
     */
    public int size() {
        return size;
    }

    /**
     * Rearranges decreased element of certain Node in the heap
     * by cutting it out of parents list, if it has parents and
     * making it a min node if it is lower than the min node or
     * if the node is going to be deleted soon
     * <br> <br>
     * <p>
     * This method is used only in {@link #modifyElement(INode, Comparable)}
     * and {@link #extract(INode)} methods, else it can misfunction and give
     * weird results for the heap itself.
     * </p>
     * <p>
     * Asymptotic time complexity = O(1) - amortized
     *
     * @param node          Node to rearrange
     * @param isToBeDeleted Whether node is going to be deleted (skips comparator then)
     * @see #modifyElement(INode, Comparable) for usage
     * @see #extract(INode) for usage
     */
    private void rearrange(Node<E> node, boolean isToBeDeleted) {
        final Node<E> parentNode = node.parent;

        if (parentNode != null && (isToBeDeleted || comparator.compare(node.element, parentNode.element) < 0)) {
            cut(node, parentNode);

            cascadingCut(parentNode);
        }

        if (isToBeDeleted || comparator.compare(node.element, min.element) < 0) {
            min = node;
        }
    }

    /**
     * Removes provided node from its list (side-links will be cut)
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param node Node to remove
     */
    private void removeSideLinks(Node<E> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    /**
     * Inserts {@nodeToInsert} after {@node} in the set
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param nodeToInsert Node to insert after the {@node}
     * @param node         Node after which to insert {@nodeToInsert}
     */
    private void insertAfter(Node<E> nodeToInsert, Node<E> node) {
        nodeToInsert.left = node;
        nodeToInsert.right = node.right;
        node.right = nodeToInsert;
        nodeToInsert.right.left = nodeToInsert;
    }

    /**
     * Restructures the heap by joining root-trees of the same
     * weight until there are no more roots of the same weight
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     */
    private void restructure() {
        final Node<E>[] weightArray = new Node[(int) Math.ceil((Math.log(size())) / (Math.log((1 + Math.sqrt(5.0)) / 2))) + 1];
        Node<E> rootNode = min, nextNode, succNode, tempNode;
        int rootWeight;

        while (roots > 0) {
            rootWeight = rootNode.weight;
            nextNode = rootNode.right;

            while ((succNode = weightArray[rootWeight]) != null) {
                if (comparator.compare(succNode.element, rootNode.element) < 0) {
                    tempNode = succNode;
                    succNode = rootNode;
                    rootNode = tempNode;
                }

                link(succNode, rootNode);

                weightArray[rootWeight++] = null;
            }

            weightArray[rootWeight] = rootNode;
            rootNode = nextNode;
            roots--;
        }

        min = null;

        for (Node<E> node : weightArray) {
            if (node == null) {
                continue;
            }

            if (min == null) {
                min = node;
            } else {
                removeSideLinks(node);

                insertAfter(node, min);

                if (comparator.compare(node.element, min.element) < 0) {
                    min = node;
                }
            }

            roots++;
        }
    }

    public static final class Node<E> implements INode<E> {

        private Node<E> child;
        private E element;
        private Node<E> left;
        private boolean marked;
        private Node<E> parent;
        private Node<E> right;
        private int weight;

        private Node(E element) {
            child = null;
            this.element = element;
            left = this;
            marked = false;
            parent = null;
            right = this;
            weight = 0;
        }

        /**
         * Returns the element of the node
         *
         * @return Element
         */
        public E getElement() {
            return element;
        }

        /**
         * Transforms node into a string with its element and its weight.
         * If node is marked, then the string contains `*` symbol
         *
         * @return String representation of the node
         */
        @Override
        public String toString() {
            return element == null ? "<?>" : element + " | " + (marked ? "* " : "") + "↓" + weight;
        }
    }
}