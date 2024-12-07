class Node implements Comparable<Node> {
    String id;
    int dist;

    public Node(String id, int dist) {
        this.id = id;
        this.dist = dist;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.dist, other.dist);
    }
    // dist 기준으로 판단 (Priority Queue)
}