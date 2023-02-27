package offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 给定一个不含重复数字的数组 nums，返回其所有可能的全排列。按任意顺序返回答案 */
public class Permute {

    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        List<Integer> output = new ArrayList<>();
        for (int num : nums) {
            output.add(num);
        }
        backtrack(nums.length, output, result, 0);
        return result;
    }

    private static void backtrack(
            int n, List<Integer> output, List<List<Integer>> result, int first) {
        if (first == n) {
            result.add(new ArrayList<>(output));
        }
        for (int i = first; i < n; i++) {
            Collections.swap(output, first, i);
            backtrack(n, output, result, first + 1);
            Collections.swap(output, i, first);
        }
    }

    private static void print(List<List<Integer>> result) {
        for (List<Integer> output : result) {
            System.out.print("[");
            for (int j = 0; j < output.size(); j++) {
                System.out.print(output.get(j));
                if (j == output.size() - 1) {
                    break;
                }
                System.out.print(",");
            }
            System.out.println("]");
        }
    }

    public static void main(String[] args) {
        int[] nums = new int[] {1, 2, 3};
        List<List<Integer>> lists = permute(nums);
        print(lists);
    }
}
