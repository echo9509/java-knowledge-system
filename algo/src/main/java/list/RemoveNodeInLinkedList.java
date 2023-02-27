package list;

public class RemoveNodeInLinkedList {

    /**
    * 删除倒数第K个节点
    *
    * @param node
    * @param k
    * @param <T>
    * @return
    */
    public static <T> Node<T> removeNode(Node<T> node, int k) {
        Node<T> fast = node;
        int i = 1;
        while (fast != null && i < k) {
            fast = fast.next;
            i++;
        }
        if (fast == null) {
            return node;
        }

        Node<T> slow = node;
        Node<T> pre = null;
        while (fast.next != null) {
            fast = fast.next;
            pre = slow;
            slow = slow.next;
        }
        if (pre == null) {
            node = node.next;
        } else {
            pre.next = pre.next.next;
        }
        return node;
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

        public Node() {}

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
        Node<Integer> node6 = new Node<>(9);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        System.out.println("删除前链表：");
        printAll(node1);
        removeNode(node1, 2);
        System.out.println("删除后链表：");
        printAll(node1);
    }
}
