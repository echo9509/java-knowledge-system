package offer;

import java.util.HashSet;
import java.util.Set;

/**
 * 剑指 Offer 03. 数组中重复的数字
 * 在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。
 */
public class DuplicateNumberInArray {

    public static int findRepeatNumber(int[] nums) {
        Set<Integer> key = new HashSet<>();
        for (int num : nums) {
            if (!key.add(num)) {
                return num;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] data = new int[]{2, 3, 1, 0, 2, 5, 3};
        System.out.println(findRepeatNumber(data));
    }
}
