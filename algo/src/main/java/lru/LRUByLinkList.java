package lru;

import java.util.Objects;
import java.util.Scanner;

/**
 * 单向链表实现LRU淘汰算法
 * 越靠近尾部访问时间越早
 */
public class LRUByLinkList<T> {

    private final static int DEFAULT_CAPACITY = 10;

    /**
     * 头结点
     */
    private Node<T> headNode;

    /**
     * 当前链表长度
     */
    private int length;

    /**
     * 链表总容量
     */
    private final int capacity;

    public LRUByLinkList() {
        this(DEFAULT_CAPACITY);
    }

    public LRUByLinkList(int capacity) {
        this.capacity = capacity;
        this.length = 0;
        headNode = new Node<>();
    }

    /**
     * 将数据加入缓存
     * @param data
     */
    public void putNode(T data) {
        Node<T> node = findPreNode(data);
        if (node != null) {
            // 如果数据已在节点列表中，移除数据节点
            deleteNextNode(node);
        }
        // 将数据节点放置到头结点
        insertDataToHead(data);
    }

    /**
     * 将节点插入头结点
     *
     * @param data
     */
    private void insertDataToHead(T data) {
        headNode.setNext(new Node<>(data, headNode.getNext()));
        length++;
        if (!overCapacity()) {
            return;
        }
        deleteTailNode();
    }

    /**
     * 删除尾结点
     */
    private void deleteTailNode() {
        Node<T> node = headNode;
        if (node.getNext() == null) {
            return;
        }
        while (node.getNext().getNext() != null) {
            node = node.getNext();
        }
        node.setNext(null);
        length--;
    }

    /**
     * 是否超过容量
     *
     * @return
     */
    private boolean overCapacity() {
        return length > this.capacity;
    }

    /**
     * 删除node节点的下一个节点
     *
     * @param node
     */
    private void deleteNextNode(Node<T> node) {
        Node<T> next = node.getNext();
        if (Objects.isNull(next)) {
            return;
        }
        node.setNext(next.getNext());
        length--;
    }

    /**
     * 获取元素的前一个节点
     *
     * @param data
     * @return
     */
    private Node<T> findPreNode(T data) {
        Node<T> node = headNode;
        while (!Objects.isNull(node.getNext())) {
            if (data.equals(node.getNext().getData())) {
                return node;
            }
            node = node.getNext();
        }
        return null;
    }

    private void printAllData() {
        Node<T> node = headNode.getNext();
        while (!Objects.isNull(node)) {
            System.out.print(node.getData() + ",");
            node = node.getNext();
        }
        System.out.println();
    }


    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node() {
        }

        public Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public Node<T> getNext() {
            return next;
        }
    }

    public static void main(String[] args) {
        LRUByLinkList<Integer> list = new LRUByLinkList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            list.putNode(scanner.nextInt());
            list.printAllData();
        }
    }
}
