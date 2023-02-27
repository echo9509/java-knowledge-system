package list;

import java.util.Objects;

public class RingInLinkedList {

    /**
    * 检测链表中是否有环
    *
    * @param node
    * @param <T>
    * @return
    */
    public static <T> boolean ringInNode(Node<T> node) {
        if (Objects.isNull(node) || Objects.isNull(node.next)) {
            return false;
        }
        Node<T> slowNode = node;
        Node<T> fastNode = node.next.next;
        while (slowNode != fastNode) {
            if (slowNode.next == null || fastNode.next == null) {
                return false;
            }
            slowNode = slowNode.next;
            fastNode = fastNode.next.next;
        }
        return true;
    }

    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node() {}

        public Node(T data) {
            this.data = data;
        }
    }

    /**
    * 检测链表中是否有环
    *
    * @param args
    */
    public static void main(String[] args) {
        Node<Integer> node1 = new Node<>(1);
        Node<Integer> node2 = new Node<>(2);
        Node<Integer> node3 = new Node<>(3);
        Node<Integer> node4 = new Node<>(4);
        Node<Integer> node5 = new Node<>(5);
        // 构造无环链表
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        System.out.println("链表" + (ringInNode(node1) ? "有环" : "无环"));
        // 设置成有环链表
        node5.next = node4;
        System.out.println("链表" + (ringInNode(node1) ? "有环" : "无环"));
    }
}
