package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 *  A Binary Search Tree BST has the property:
 *  left child of a node is always less than the node
 *  right child of a node is always more than the node
 */
public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V>, Iterable<K>{

    private BSTNode root;
    int size = 0;

    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void clear() {
        // just setting up all the left and right node to null
        root = clear(root);
    }

    private BSTNode clear(BSTNode node) {
        if (node == null) {
            return null;
        }
        node.left = clear(node.left);
        node.right = clear(node.right);
        node = null;
        size--;
        return node;
    }

    @Override
    public boolean containsKey(K key) {
        if(get(root, key) == null) {
            return false;
        }
        return true;
    }

    @Override
    public V get(K key) {
        if(root == null) {
            return null;
        }
        // Find node with the key, return value
        BSTNode node = get(root, key);
        return node.value;
    }
    private BSTNode get(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        }
        return node;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            size++;
            return new BSTNode(key, value);
        } else {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node.left = put(node.left, key, value);
            } else if (cmp == 0) {
                node.value = value;
            } else {
                node.right = put(node.right, key, value);
            }
            return node;
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        root = remove(root, key);
        return value;
    }

    private BSTNode remove(BSTNode node, K key) {
        if(node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            // no child and one chile
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }
            // two child
            // find predecessor
            BSTNode temp = node;
            node = min(node.right);
            node.right = deleteMin(temp.right);
            node.left = temp.left;
        }
        // return root
        return node;
    }

    private BSTNode deleteMin(BSTNode node) {
        if (node.left == null) {
            size--;
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }

    private BSTNode max(BSTNode node) {
        if (node.right == null) {
            return node;
        }
        return max(node.right);
    }

    private BSTNode min(BSTNode node) {
        if (node.left == null) {
            return node;
        }
        return min(node.left);
    }

    @Override
    public V remove(K key, V value) {
        V val = get(key);
        if (val != value) {
            return null;
        }
        root = remove(root, key);
        return null;
    }

    private class BSTmapIterator<K, V> implements Iterator<K> {
        private BSTNode current;
        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public K next() {
            return null;
        }
    }
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        throw new UnsupportedOperationException();
    }
}

