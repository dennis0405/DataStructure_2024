import java.util.LinkedList;

class HashTable<K extends Comparable<K>, V> {
    private final AVLTree<K, V>[] table;

    @SuppressWarnings("unchecked")
    public HashTable() {
        int SIZE = 100;
        table = new AVLTree[SIZE];
        for (int i = 0; i < SIZE; i++) {
            table[i] = new AVLTree<>();
        }
    }

    private int hash(K key) {
        if (key instanceof String) {
            int asciiSum = 0;
            for (char c : ((String) key).toCharArray()) {
                asciiSum += c;
            }
            return asciiSum % 100;
        } else {
            return key.hashCode() % 100;
        }
    }

    public void insert(K key, V position) {
        int slot = hash(key);
        table[slot].insert(key, position);
    }

    public LinkedList<V> search(K key) {
        int slot = hash(key);
        return table[slot].search(key);
    }

    public void printSlot(int index) {
        if (table[index].isEmpty()) {
            System.out.println("EMPTY");
        } else {
            StringBuilder substrings = table[index].printPreOrder();
            //맨 끝 공백 제거
            substrings.deleteCharAt(substrings.length() - 1);
            System.out.println(substrings);
        }
    }
}