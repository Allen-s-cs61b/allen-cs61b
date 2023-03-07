package deque;

public class ArrayDeque<T> {
    private int nextFirst;
    private int nextLast;
    private int size;
    public T[] array;

    /** Constructor, starting size is 8, and starting at 4, 5 */
    public ArrayDeque() {
        //create a generic T array
        array = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
    }

    /** Add to the first place */
    public void addFirst(T value) {
        array[nextFirst] = value;
        size++;
        setFirst(nextFirst);
    }
    /** Helper method setting the nextFirst */
    private void setFirst(int n) {
        if(size == array.length - 1) {
            //resize array
        }
        //if it is index 0, the nextFirst is the length-1 */
        if(n == 0) {
            this.nextFirst = array.length - 1;
        } else {
            this.nextFirst -= 1;
        }
    }

    /** Add to the last place */
    public void addLast(T value) {
        array[nextLast] = value;
        size++;
        setLast(nextLast);
    }
    /** Helper method for setting nextLast */
    private void setLast(int n) {
        if(size == array.length - 1) {
            //resize array
        }
        if(n == array.length - 1) {
            this.nextLast = 0;
        } else {
            this.nextLast += 1;
        }
    }

    /** Remove the last value in the list */
    public T removeLast() {
        if(size == 0) {
            return null;
        }
        return removeLast(nextLast);
    }
    private T removeLast(int n) {
        size--;
        if(nextLast == 0) {
            nextLast = array.length - 1;
        } else {
            nextLast -= 1;
        }
        T temp = array[nextLast];
        array[nextLast] = null;
        if(size < array.length / 4) {
            //resize
        }
        return temp;
    }

    /** Remove the last value in the list */
    public T removeFirst() {
        if(size == 0) {
            return null;
        }
        return removeFirst(nextFirst);
    }
    private T removeFirst(int n) {
        size--;
        if(nextFirst == array.length - 1) {
            nextFirst = 0;
        } else {
            nextFirst += 1;
        }
        T temp = array[nextFirst];
        array[nextFirst] = null;
        if(size < array.length / 4) {
            //resize
        }
        return temp;
    }

    /** Return the size of the arrayList */
    public int size() {
        return size;
    }

    /** Return true if it is empty */
    public boolean isEmpty() {
        if(size == 0) {
            return true;
        }
        return false;
    }

    /** Return the nth value in the arrayList */
    public T get(int n) {
        if(n >= size() || n < 0) {
            return null;
        }
        int index = n + 1 + nextFirst;
        if(index > array.length-1) {
            index = index - array.length;
        }
        return array[index];
    }

    /** Print out the arrayList from nextFirst + 1 to nextLast -1 */
    public void printDeque() {
        // Check if nextFirst is at the front of nextLast
        if(nextFirst < nextLast) {
            for (int i = nextFirst + 1; i < nextLast; i++) {
                System.out.print(array[i].toString() + " ");
            }
        } else {
            for (int i = nextFirst + 1; i < array.length; i++) {
                System.out.print(array[i].toString() + " ");
            }
            for (int i = 0; i < nextLast; i++) {
                System.out.print(array[i].toString() + " ");
            }
        }
        System.out.println();
    }
    public static void main(String args[]) {

    }
}
