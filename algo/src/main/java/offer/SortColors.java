package offer;

import java.util.Arrays;

/**
* 给定一个包含红色、白色和蓝色、共 n 个元素的数组 nums，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。 我们使用整数 0、 1 和 2
* 分别表示红色、白色和蓝色。
*/
public class SortColors {

    public static void sortColors(int[] nums) {

        int p0 = 0;
        int p1 = 0;

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                swap(nums, p0, i);
                if (p0 < p1) {
                    swap(nums, p1, i);
                }
                p0++;
                p1++;
                continue;
            }
            if (nums[i] == 1) {
                swap(nums, p1, i);
                p1++;
            }
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private static void print(int[] nums) {
        Arrays.stream(nums).forEach(System.out::println);
    }

    public static void main(String[] args) {
        int[] nums = new int[] {2, 0, 2, 1, 1, 0};
        sortColors(nums);
        print(nums);
    }
}
