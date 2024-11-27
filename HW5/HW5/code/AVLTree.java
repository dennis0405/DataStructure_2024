import java.util.*;

public class AVLTree<K extends Comparable<K>, V> {
    private AVLNode<K, V> root;
    static final AVLNode<?, ?> NIL = new AVLNode<>();

    @SuppressWarnings("unchecked")
    public AVLTree() {
        root = (AVLNode<K, V>) NIL;
    }

    public void insert(K key, V position) {
        root = insertItem(root, key, position);
    }

    private AVLNode<K, V> insertItem(AVLNode<K, V> node, K key, V position) {
        if (node == NIL) {
            return new AVLNode<>(key, position);
        }

        // insert substring in alphabetical order
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertItem(node.left, key, position);
        } else if (cmp > 0) {
            node.right = insertItem(node.right, key, position);
        } else {
            // if it is same substring
            node.positions.add(position);
            // 높이 변화가 없으니까 바로 return
            return node;
        }

        // 높이 조정
        node.height = 1 + Math.max(node.left.height, node.right.height);

        // 균형 확인 후 회전
        int balance = getBalance(node);

        if (balance > 1) {
            //Case LL
            if ( getBalance(node.left) >= 0 ) return rotateRight(node);
                //Case LR
            else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }

        if (balance < -1) {
            //Case RR
            if( getBalance(node.right) <= 0) return rotateLeft(node);
                //Case RL
            else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    private int getBalance(AVLNode<K, V> node) {
        return node.left.height - node.right.height;
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> t) {
        AVLNode<K, V> LChild = t.left;
        AVLNode<K, V> LRChild = LChild.right;
        LChild.right = t;
        t.left = LRChild;
        t.height = 1 + Math.max(t.left.height, t.right.height);
        LChild.height = 1 + Math.max(LChild.left.height, LChild.right.height);
        return LChild;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> t) {
        AVLNode<K, V> RChild = t.right;
        AVLNode<K, V> RLChild = RChild.left;
        RChild.left = t;
        t.right = RLChild;
        t.height = 1 + Math.max(t.left.height, t.right.height);
        RChild.height = 1 + Math.max(RChild.left.height, RChild.right.height);
        return RChild;
    }

    public LinkedList<V> search(K key) {
        AVLNode<K, V> node = searchNode(root, key);
        return (node != NIL) ? node.positions : null;
    }

    private AVLNode<K, V> searchNode(AVLNode<K, V> node, K key) {
        if (node == NIL || key.equals(node.key)) {
            return node;
        }
        return (key.compareTo(node.key) < 0) ? searchNode(node.left, key) : searchNode(node.right, key);
    }

    public boolean isEmpty(){
        return root == NIL;
    }

    public StringBuilder printPreOrder() {
        StringBuilder preOrder = new StringBuilder();
        return printPreOrder(root, preOrder);
    }

    private StringBuilder printPreOrder(AVLNode<K, V> node, StringBuilder preOrder) {
        if (node != NIL) {
            preOrder.append(node.key).append(" ");
            printPreOrder(node.left, preOrder);
            printPreOrder(node.right, preOrder);
        }
        return preOrder;
    }
}