package list;

public class MergeOrderLinkedList {


    /**
     * 合并两个有序链表
     *
     * @param firstNode
     * @param secondNode
     * @return
     */
    public static Node<Integer> mergeOrderNode(Node<Integer> firstNode, Node<Integer> secondNode) {
        if (firstNode == null) {
            return secondNode;
        }
        if (secondNode == null) {
            return firstNode;
        }
        Node<Integer> headNode = new Node<>();
        Node<Integer> node = headNode;
        while (firstNode.next != null && secondNode.next != null) {
            if (firstNode.next.data > secondNode.next.data) {
                node.next = secondNode.next;
                secondNode.next = secondNode.next.next;
            } else {
                node.next = firstNode.next;
                firstNode.next = firstNode.next.next;
            }
            node = node.next;
        }
        if (firstNode.next == null) {
            node.next = secondNode.next;
            secondNode.next = null;
            return headNode;
        }
        node.next = firstNode.next;
        firstNode.next = null;
        return headNode;
    }

    public static <T> void printAll(Node<T> node) {
        Node<T> n = node.next;
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

    /**
     * 合并两个有序链表
     *
     * @param args
     */
    public static void main(String[] args) {
        Node<Integer> headNode = new Node<>();
        Node<Integer> node1 = new Node<>(1);
        Node<Integer> node2 = new Node<>(2);
        Node<Integer> node3 = new Node<>(4);
        Node<Integer> node4 = new Node<>(7);
        Node<Integer> node5 = new Node<>(8);
        Node<Integer> node6 = new Node<>(9);
        headNode.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        System.out.println("有序链表1：");
        printAll(headNode);

        Node<Integer> headNode1 = new Node<>();
        Node<Integer> node12 = new Node<>(1);
        Node<Integer> node11 = new Node<>(3);
        Node<Integer> node44 = new Node<>(5);
        Node<Integer> node33 = new Node<>(6);
        Node<Integer> node99 = new Node<>(7);
        headNode1.next = node12;
        node12.next = node11;
        node11.next = node44;
        node44.next = node33;
        node33.next = node99;
        System.out.println("有序链表2：");
        printAll(headNode1);

        Node<Integer> result = mergeOrderNode(headNode, headNode1);
        System.out.println("合并后的有序链表为：");
        printAll(result);
    }
}
