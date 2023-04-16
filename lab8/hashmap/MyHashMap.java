package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Allen Liang
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    // Hash table size
    private int tableSize;
    // Total items size
    private int itemSize;
    // Load factor(itemSize / tableSize)
    private double loadFactor;
    // myHashmap, which is an array of the Collection<Node>
    private Collection<Node>[] myHashMap;

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.tableSize = initialSize;
        this.loadFactor = maxLoad;
        this.itemSize = 0;
        this.myHashMap = createTable(tableSize);
    }

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for(int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    /**
     * Resize the table and rehash all the items
     */
    private void resize() {
        // create a new temporary hashmap
        MyHashMap temp = new MyHashMap(this.tableSize * 2, this.loadFactor);
        // rehash, iterate through the current hashmap and place each item in the correct bucket
        for(int i = 0; i < tableSize; i++) {
            for(Node node: myHashMap[i]){
                temp.put(node.key, node.value);
            }
        }
        this.itemSize = temp.itemSize;
        this.tableSize = temp.tableSize;
        this.myHashMap = temp.myHashMap;
    }

    private int hash(K key) {
        int hash = key.hashCode();
        hash = Math.floorMod(hash, tableSize);
        return hash;
    }

    private double currentLoadFactor() {
        return (itemSize + 1) / tableSize;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        for(Collection<Node> each : myHashMap) {
            each.clear();
        }
        itemSize = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int hash = hash(key);
        if(myHashMap[hash] == null) {
            return null;
        }
        for(Node node : myHashMap[hash]) {
            if(node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.itemSize;
    }

    @Override
    public void put(K key, V value) {
        double currentLoadFactor = currentLoadFactor();
        if(currentLoadFactor > loadFactor) {
            this.resize();
        }
        int hash = hash(key);
        for(Node node : myHashMap[hash]) {
            if(node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        Node node = createNode(key, value);
        myHashMap[hash].add(node);
        itemSize++;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for(int i = 0; i < tableSize; i++) {
            for(Node node : myHashMap[i]) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        if(containsKey(key)) {
            int hash = hash(key);
            Iterator<Node> iterator = myHashMap[hash].iterator();
            while(iterator.hasNext()) {
                Node node = iterator.next();
                if(node.key.equals(key)) {
                    V value = node.value;
                    iterator.remove();
                    itemSize--;
                    return value;
                }
            }
        }
        // Key no found
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if(containsKey(key)) {
            if(!value.equals(get(key))) {
                return null;
            }
            int hash = hash(key);
            Iterator<Node> iterator = myHashMap[hash].iterator();
            while(iterator.hasNext()) {
                Node node = iterator.next();
                if(node.key.equals(key)) {
                    iterator.remove();
                    itemSize--;
                    return value;
                }
            }
        }
        // Key no found
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }


}
