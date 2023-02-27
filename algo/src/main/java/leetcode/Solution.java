package leetcode;

import java.util.Stack;

class Solution {

    public int[] reversePrint(ListNode head) {
        Stack<Integer> values = new Stack<>();
        while (head != null) {
            values.push(head.val);
            head = head.next;
        }
        int size = values.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = values.pop();
        }
        return result;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
