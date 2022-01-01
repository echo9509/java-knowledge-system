package list;

import java.util.Objects;
import java.util.Scanner;

/**
 * 基于单向链表实现的回文字符串检测
 */
public class PalindromeByLinedList {

    /**
     * 时间复杂度O(n)
     * 空间复杂度O(n)
     * @param character
     * @return
     */
    public static boolean isPalindromeString(Character[] character) {
        Node<Character> node = build(character);
        Node<Character> reserveNode = buildReserveNode(character);
        if (Objects.isNull(node.next) || Objects.isNull(reserveNode.next)) {
            return false;
        }
        node = node.next;
        reserveNode = reserveNode.next;
        while (node.data.equals(reserveNode.data)) {
            node = node.next;
            reserveNode = reserveNode.next;
            if (Objects.isNull(node) && Objects.isNull(reserveNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 构造链表并返回头结点
     *
     * @param datas
     * @return
     */
    private static Node<Character> build(Character[] datas) {
        // 哨兵头节点
        Node<Character> headNode = new Node<>();
        Node<Character> node = headNode;
        for (Character data : datas) {
            node.next = new Node<>(data);
            node = node.next;
        }
        return headNode;
    }

    /**
     * 使用数组构建反向链表
     * @param datas
     * @return
     */
    private static Node<Character> buildReserveNode(Character[] datas) {
        // 哨兵头节点
        Node<Character> headNode = new Node<>();
        Node<Character> node = headNode;
        for (int i = datas.length -1; i >= 0; i--) {
            node.next = new Node<>(datas[i]);
            node = node.next;
        }
        return headNode;
    }


    public static class Node<T> {
        private T data;
        private Node<T> next;

        public Node() {
        }

        public Node(T data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String next = scanner.next();
            char[] chars = next.toCharArray();
            Character[] arrays = new Character[chars.length];
            for (int i = 0; i < chars.length; i++) {
                arrays[i] = chars[i];
            }
            System.out.println(next + (isPalindromeString(arrays) ? " 是回文" : " 不是回文"));
        }
    }
}
