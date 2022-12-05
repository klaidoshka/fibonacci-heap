package com.github.klaidoshka.structures;

import com.github.klaidoshka.utils.Debug;

import java.util.Comparator;

public class FibonacciHeap<E extends Comparable<E>> {

    public static final class Node<E> {

        private E element;
        private boolean marked;
        private Node<E> parent;
        private Node<E> left;
        private Node<E> right;
        private Node<E> child;
        private int weight;

        private Node(E element) {
            this.weight = 0;
            this.marked = false;
            this.parent = null;
            this.child = null;
            this.right = this;
            this.left = this;
            this.element = element;
        }

        public E getElement() {
            return element;
        }

        @Override
        public String toString() {
            return element == null ? "<?>" : element + " | " + (marked ? "* " : "") + "↓" + weight;
        }
    }

    /**
     * Smallest node inside the heap
     */
    private Node<E> min;

    /**
     * Size (amount) of entries inside the heap
     */
    private int size;

    /**
     * Roots amount inside the heap
     */
    private int roots;

    /**
     * Comparator for heap's entries arrangement
     */
    private final Comparator<E> comparator;

    /**
     * Empty constructor which uses default comparator of natural order
     * for heap's entries
     */
    public FibonacciHeap() {
        this.comparator = Comparator.naturalOrder();
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
    public static <E extends Comparable<E>> FibonacciHeap<E> init() {
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
    public static <E extends Comparable<E>> FibonacciHeap<E> init(Comparator<E> comparator) {
        return new FibonacciHeap<>(comparator);
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
     * Gets the smallest node in the heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @return Smallest heap node
     */
    public Node<E> getMin() {
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

        if (isEmpty()) min = node;
        else {
            insertAfter(node, min);
            if (comparator.compare(node.element, min.element) < 0)
                min = node;
        }

        return node;
    }

    /**
     * Deletes provided node from the heap by decreasing its key to
     * the lowest tier and removing the minimum
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @param node Node to delete from the heap
     * @return Deleted node
     */
    public Node<E> delete(Node<E> node) {
        rearrange(node, true);
        return deleteMin();
    }

    /**
     * Deletes the smallest node in the heap
     * <p>
     * Asymptotic time complexity = O(log n) - amortized
     *
     * @return Deleted node
     */
    public Node<E> deleteMin() {
        if (min == null) return null;

        final Node<E> nodeToDelete = min;

        // every 'min' node children should be transferred into heap's root list
        // by cutting them out from the parent 'min'
        while (nodeToDelete.child != null)
            cut(nodeToDelete.child, nodeToDelete, false);

        // remove 'min' node from the roots list
        removeSideLinks(nodeToDelete);

        // if node links into itself, then no other nodes are left
        if (nodeToDelete == nodeToDelete.right) clear();
        else {
            min = nodeToDelete.right;
            size--;
            roots--;
            restructure();
        }

        return nodeToDelete;
    }

    /**
     * Merges provided heap into the current heap
     * <p>
     * Asymptotic time complexity = O(1)
     *
     * @param heap Heap to use for the merge
     */
    public void merge(FibonacciHeap<E> heap) {
        if (heap == null || heap.isEmpty()) return;

        size += heap.size;
        roots += heap.roots;

        if (this.min == null) min = heap.min;
        else {
            final Node<E> rightNode = min.right;
            final Node<E> anotherRightNode = heap.min.right;

            // merging the heaps
            min.right = anotherRightNode;
            anotherRightNode.left = min;
            rightNode.left = heap.min;
            heap.min.right = rightNode;

            // updating the min pointer
            if (comparator.compare(heap.min.element, min.element) < 0)
                min = heap.min;
        }
    }

    /**
     * Decreases element of certain Node in the heap
     * <br>
     * <b>Element - Must be lower than the current one</b>
     * <p>
     * Asymptotic time complexity = O(1) - amortized
     *
     * @param node    Node to decrease element for
     * @param element Element
     */
    public void decreaseElement(Node<E> node, E element) {
        if (element == null)
            throw new Error("Element is null at FibonacciHeap#decreaseElement.");

        if (comparator.compare(node.element, element) < 0)
            throw new Error("Element is greater than previous one at FibonacciHeap#decreaseElement.");

        node.element = element;

        rearrange(node, false);
    }

    /**
     * Rearranges decreased element of certain Node in the heap
     * by cutting it out of parents list, if it has parents and
     * making it a min node if it is lower than the min node or
     * if the node is going to be deleted soon
     * <br> <br>
     * <p>
     * This method is used only in {@link #decreaseElement(Node, Comparable)}
     * and {@link #delete(Node)} methods, else it can misfunction and give
     * weird results for the heap itself.
     * </p>
     * <p>
     * Asymptotic time complexity = O(1) - amortized
     *
     * @param node          Node to rearrange
     * @param isToBeDeleted Whether node is going to be deleted (skips comparator then)
     * @see #decreaseElement(Node, Comparable) for usage
     * @see #delete(Node) for usage
     */
    private void rearrange(Node<E> node, boolean isToBeDeleted) {
        final Node<E> parentNode = node.parent;

        // if parent's child has lower key than parent, execute this
        if (parentNode != null && (isToBeDeleted || comparator.compare(node.element, parentNode.element) < 0)) {
            cut(node, parentNode);
            cascadingCut(parentNode);
        }

        if (isToBeDeleted || comparator.compare(node.element, min.element) < 0) min = node;
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

            // look for a node of the same degree and make
            // one of them the child of the another one
            while ((succNode = weightArray[rootWeight]) != null) {
                // find out who is the parent by comparator
                if (comparator.compare(succNode.element, rootNode.element) < 0) {
                    tempNode = succNode;
                    succNode = rootNode;
                    rootNode = tempNode;
                }

                // link succNode as a child to the rootNodePointer
                link(succNode, rootNode);

                // set current degree as null and move on
                weightArray[rootWeight++] = null;
            }

            // save the node for later to be used in above <while> cycle
            weightArray[rootWeight] = rootNode;
            rootNode = nextNode;
            roots--;
        }

        // reset the roots list and reconstruct it from weightArray
        min = null;

        for (Node<E> node : weightArray) {
            if (node == null) continue;

            // add starting root node
            if (min == null) min = node;
            else { // remove any side links and put it as a root
                removeSideLinks(node);
                insertAfter(node, min);
                if (comparator.compare(node.element, min.element) < 0) min = node;
            }

            roots++;
        }
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
        // delete @child from the heap's root list
        removeSideLinks(child);

        // make @child a child of parent
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

        // add up weight, set marked to false
        parent.weight++;
        child.marked = false;
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
        // remove nodeToCut from parentNode's child list
        removeSideLinks(nodeToCut);
        parentNode.weight--;

        if (parentNode.child == nodeToCut) {
            if (nodeToCut != nodeToCut.right)
                parentNode.child = nodeToCut.right;
            else parentNode.child = null;
        }

        // add @nodeToCut to the heap's roots list
        insertAfter(nodeToCut, min);
        roots++;

        // set default values
        nodeToCut.parent = null;
        if (unmark) nodeToCut.marked = false;
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

        if (parent == null) return;

        // if node is not marked, stop the cutting and mark it
        if (!nodeToCutOut.marked) nodeToCutOut.marked = true;
        else {
            cut(nodeToCutOut, parent); // the node is marked, so cut it out from the parent
            cascadingCut(parent); // keep cutting the nodes up...
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
        node.left.right = node.right; // Remove left node's link
        node.right.left = node.left; // Remove right node's link
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

    /*
    -----------------------------------------------------------------------------------------------------------
     */
    public void display() {
        Debug.log(" HEAP SHOWCASE * " + size + " Entries [ Minimum: " + (min == null ? "<?>" : min) + " ]");
        if (isEmpty()) Debug.log("  -> HEAP IS EMPTY");
        else display(min, " ");
    }

    private void display(Node<E> node, String prefix) {
        if (node == null) return;
        Node<E> temp = node;
        Node<E> k;
        do {
            Debug.log(new StringBuilder(prefix)
                    .append(" -> ")
                    .append(temp.toString())
                    .toString()
            );
            k = temp.child;
            display(k, prefix + "   ");
            temp = temp.right;
        } while (temp != node);
    }
}