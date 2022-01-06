package sort;

import java.util.Objects;

public class Sort {

    /**
     * 选择排序
     * 时间复杂度O(n²)
     * 空间复杂度O(1)
     * @param arrays
     */
    public static void selectionSort(int[] arrays) {
        for (int i = 0; i < arrays.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arrays.length; j++) {
                if (arrays[j] < arrays[min]) {
                    min = j;
                }
            }
            if (min != i) {
                int temp = arrays[i];
                arrays[i] = arrays[min];
                arrays[min] = temp;
            }
        }
    }

    /**
     * 插入排序
     * 时间复杂度O(n²)
     * 空间复杂度O(1)
     * @param arrays
     */
    public static void insertionSort(int[] arrays) {
        if (Objects.isNull(arrays) || arrays.length <= 1) {
            return;
        }
        for (int i = 1; i < arrays.length; i++) {
            int value = arrays[i];
            int j = i - 1;
            for (; j >= 0 ; j--) {
                if (arrays[j] > value) {
                    arrays[j + 1] = arrays[j];
                } else {
                    break;
                }
            }
            arrays[j + 1] = value;
        }
    }

    /**
     * 冒泡排序
     * 时间复杂度：O(n²)
     * 空间复杂度：O(1)
     * @param arrays
     */
    public static void bubbleSort(int[] arrays) {
        if (Objects.isNull(arrays) || arrays.length <= 1) {
            return;
        }
        for (int i = 0; i < arrays.length; i++) {
            // 是否有数据交换
            boolean flag = false;
            for (int j = 0; j < arrays.length - i - 1; j++) {
                if (arrays[j] > arrays[j + 1]) {
                    int temp = arrays[j];
                    arrays[j] = arrays[j + 1];
                    arrays[j + 1] = temp;
                    flag = true;
                }
            }
            if (!flag) {
                break;
            }
        }
    }

    public static void print(int[] arrays) {
        if (Objects.isNull(arrays) || arrays.length == 0) {
            return;
        }
        System.out.println("数组内容为：");
        for (int item : arrays) {
            System.out.print(item + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int [] arrays = new int[]{
          7,1,3,45,6,7,8,9,1,3,2
        };
//        System.out.print("冒泡排序前");
//        print(arrays);
//        bubbleSort(arrays);
//        System.out.print("冒泡排序后");
//        print(arrays);

//        System.out.print("插入排序前");
//        print(arrays);
//        insertionSort(arrays);
//        System.out.print("插入排序后");
//        print(arrays);

        System.out.print("选择排序前");
        print(arrays);
        selectionSort(arrays);
        System.out.print("选择排序后");
        print(arrays);
    }
}
