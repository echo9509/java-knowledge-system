package list;

/**
 * 反置链表
 */
public class ReservedLinkedList {

    /**
     * 反置链表
     * @param node
     * @param <T>
     * @return
     */
    public static <T> Node<T> reverseNode(Node<T> node) {
        Node<T> headNode = new Node<>();
        Node<T> current = node.next;
        while (current != null) {
            node.next = current.next;
            current.next = headNode.next;
            headNode.next = current;
            current = node.next;
        }
        return headNode;
    }

    /**
     * 构建链表
     * @param datas
     * @param <T>
     * @return
     */
    public static <T> Node<T> buildNode(T[] datas) {
        Node<T> headNode = new Node<>();
        Node<T> node = headNode;
        for (T data : datas) {
            node.next = new Node<>(data);
            node = node.next;
        }
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
     * 反置链表
     * @param args
     */
    public static void main(String[] args) {
        Integer [] test = new Integer[]{1,3,5,6,7,8,1};
        Node<Integer> node = buildNode(test);
        System.out.println("正向链表：");
        printAll(node);
        node = reverseNode(node);
        System.out.println("反置链表：");
        printAll(node);
    }
}
