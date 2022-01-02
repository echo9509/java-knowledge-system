package list;


public class MidNodeInLinkedList {

    /**
     * 求链表中间节点
     * @param node
     * @param <T>
     * @return
     */
    public static <T> Node<T> midNode(Node<T> node) {
        if (node == null || node.next == null || node.next.next == null) {
            return node;
        }
        Node<T> slow = node;
        Node<T> fast = node;
        while (fast!= null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private static <T> void printAll(Node<T> node) {
        Node<T> n = node;
        while (n != null) {
            System.out.print(n.data + ",");
            n = n.next;
        }
        System.out.println();
    }


    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node() {
        }

        public Node(T data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        Node<Integer> node1 = new Node<>(1);
        Node<Integer> node2 = new Node<>(2);
        Node<Integer> node3 = new Node<>(4);
        Node<Integer> node4 = new Node<>(7);
        Node<Integer> node5 = new Node<>(8);
        Node<Integer> node6 = new Node<>(10);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        System.out.println("链表：");
        printAll(node1);
        Node<Integer> midNode = midNode(node1);
        System.out.println("链表中间节点的值为：");
        System.out.println(midNode.data);
    }
}
