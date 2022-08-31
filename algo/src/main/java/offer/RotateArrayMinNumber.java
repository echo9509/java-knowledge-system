package offer;

/**
 * @author sh
 * 旋转数组的最小数字
 *
 * https://leetcode.cn/problems/xuan-zhuan-shu-zu-de-zui-xiao-shu-zi-lcof/?favorite=xb9nqhhg
 */
public class RotateArrayMinNumber {

    public static int minArray(int[] numbers) {
        int low = 0, high = numbers.length - 1;
        while (low < high) {
            int pivot = low + (high - low) / 2;
            if (numbers[pivot] < numbers[high]) {
                high = pivot;
            } else if (numbers[pivot] > numbers[high]) {
                low = pivot + 1;
            } else {
                high -= 1;
            }
        }
        return numbers[low];
    }

    public static void main(String[] args) {
        int[] numbers = new int[]{1,3,5};
        System.out.println(minArray(numbers));
    }
}
