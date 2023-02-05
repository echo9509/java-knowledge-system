package leetcode;


public class Offer24 {

    public ListNode reverseList(ListNode head) {
        ListNode result = null;
        while (head != null) {
            ListNode temp = result;
            result = new ListNode(head.val);
            result.next = temp;
            head = head.next;
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
