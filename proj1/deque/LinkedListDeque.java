package deque;

public class LinkedListDeque<T> {
    /** Nested Node class */
    public class Node {
        public T value;
        public Node next;
        public Node previous;
        public Node(T v, Node n, Node p) {
            value = v;
            next = n;
            previous = p;
        }
    }

    public Node sentinel;
    public int size;
    /** Constructor
     *  Sentinel has to point back to itself when initializing
     */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    public LinkedListDeque(T valueSentinel, T value) {
        sentinel = new Node(valueSentinel, null, null);
        sentinel.next = sentinel.previous = new Node(value, sentinel, sentinel);
        size = 1;
    }

    /** Add a node to the start of the list (after sentinel) */
    public void addFirst(T value) {
        size++;
        Node temp = sentinel.next;
        sentinel.next = new Node(value, sentinel.next, sentinel);
        temp.previous = sentinel.next;
        sentinel.previous = temp;
    }

    /** Add a node to the end of the list */
    public void addLast(T value) {
        size++;
        Node temp = sentinel.previous;
        sentinel.previous = new Node(value, sentinel, sentinel.previous);
        temp.next = sentinel.previous;
    }

    /** Remove a node from the start of the list (after sentinel)
     * if sentinel is the only node left, just return
     */
    public T removeFirst() {
        if(size == 0) {
            return null;
        }
        size--;
        T temp = sentinel.next.value;
        sentinel.next = sentinel.next.next;
        sentinel.next.previous = sentinel;
        return temp;
    }

    /** Remove a node from the start of the list
     * if sentinel is the only node left, just return
     */
    public T removeLast() {
        if(size == 0) {
            return null;
        }
        size--;
        T temp = sentinel.previous.value;
        sentinel.previous = sentinel.previous.previous;
        sentinel.previous.next = sentinel;
        return temp;
    }

    /** Return true if the deque is empty */
    public boolean isEmpty() {
        if(size > 0) {
            return false;
        }
        return true;
    }

    /** Return the nth value in the list */
    public T get(int index) {
        if(index >= size || index < 0) {
            return null;
        }
        int n= 0;
        Node temp = sentinel;
        while(n <= index) {
            temp = temp.next;
            n++;
        }
        return temp.value;
    }
    public T getRecursive(int index) {
        Node temp = sentinel.next;
        return getRecursive(temp, index);
    }
    /** Helper method */
    public T getRecursive(Node node, int index) {
        if(index == 0) {
            return node.value;
        }
        return getRecursive(node.next, index - 1);
    }

    /** Return the size of the list(excluding the sentinel) */
    public int size() {
        return size;
    }

    /** Print the Deque */
    public void printDeque() {
        int n = 0;
        Node temp = sentinel;
        while(n < size) {
            temp = temp.next;
            System.out.print(temp.value + " ");
            n++;
        }
        System.out.println();
    }

    /** Return true if the object o is equal to the linkedlistDeque
    public boolean equals(Object o) {

    }
    */
     /**
    public Iterator<T> iterator() {

    }
     */

    public static void main(String args[]) {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        L.addFirst(45);
        L.addFirst(35);
        L.addLast(55);
        L.addLast(65);
        L.removeLast();
        L.printDeque();
        L.getRecursive(3);
        L.get(3);
    }
}
