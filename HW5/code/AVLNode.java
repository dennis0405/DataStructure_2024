import java.util.LinkedList;

public class AVLNode<K extends Comparable<K>, V> {
    public K key;
    public LinkedList<V> positions;
    public int height;
    public AVLNode<K, V> left, right;

    @SuppressWarnings("unchecked")
    public AVLNode(K key, V position) {
        this.key = key;
        this.positions = new LinkedList<>();
        this.positions.add(position);
        this.height = 1;
        this.left = this.right = (AVLNode<K, V>) AVLTree.NIL;
    }

    // NIL Node Constructor
    public AVLNode() {
        this.key = null;
        this.positions = null;
        this.left = null;
        this.right = null;
        this.height = 0;
    }

}