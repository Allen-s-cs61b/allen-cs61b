package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /** Test the add methods */
    public void addTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        //L.addFirst(10);
        L.addFirst(5);
        L.addFirst(0);
        L.addLast(15);
        L.addLast(20);
        L.addLast(25);
        L.addLast(30);
        int expected = 0;
        int actual = L.get(3);
        L.printDeque();
        assertEquals(expected, actual);
        assertEquals(null, L.get(2));
    }

    @Test
    /** Test the remove methods */
    public void removeTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        //L.addFirst(10); 0, 5, 15, 20, 25, 30
        L.addFirst(5);
        L.addFirst(0);
        L.addLast(15);
        L.addLast(20);
        L.addLast(25);
        L.addLast(30);
        L.printDeque();
        L.removeLast();
        L.removeFirst();
        L.printDeque();
        int expected = 25;
        int actual = L.get(3);
        assertEquals(expected, actual);
        assertEquals(4, L.size());
    }

    @Test
    /** Test the get methods */
    public void getTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        //L.addFirst(10); 15, 20, 25, 30, 35
        L.addLast(15);
        L.addLast(20);
        L.addLast(25);
        L.addLast(30);
        L.addLast(35);
        int expected = 35;
        int actual = L.get(4);
        assertEquals(expected, actual);
    }
}
