package sort;

import java.util.Objects;

public class Sort {

    /**
    * 计数排序
    *
    * @param arrays
    */
    public static void countingSort(int[] arrays) {
        int n = arrays.length;
        if (n <= 1) {
            return;
        }
        int max = arrays[0];
        for (int i = 1; i < n; i++) {
            if (max < arrays[i]) {
                max = arrays[i];
            }
        }
        // 准备计数数组
        int[] c = new int[max + 1];
        for (int i = 0; i < n; i++) {
            c[arrays[i]]++;
        }
        for (int i = 1; i <= max; i++) {
            c[i] = c[i - 1] + c[i];
        }
        int[] r = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int index = c[arrays[i]] - 1;
            r[index] = arrays[i];
            c[arrays[i]]--;
        }
        for (int i = 0; i < n; i++) {
            arrays[i] = r[i];
        }
    }

    /**
    * 快速排序 时间复杂度O(logn) 空间复杂度O(1)
    *
    * @param arrays
    */
    public static void quickSort(int[] arrays) {
        quickSort(arrays, 0, arrays.length - 1);
    }

    private static void quickSort(int[] arrays, int start, int end) {
        if (start >= end) {
            return;
        }
        int mid = partition(arrays, start, end);
        quickSort(arrays, start, mid - 1);
        quickSort(arrays, mid + 1, end);
    }

    private static int partition(int[] arrays, int start, int end) {
        int pivot = arrays[end];
        int i = start;
        for (int j = start; j < end; j++) {
            if (arrays[j] < pivot) {
                int temp = arrays[i];
                arrays[i] = arrays[j];
                arrays[j] = temp;
                i++;
            }
        }
        int temp = arrays[i];
        arrays[i] = arrays[end];
        arrays[end] = temp;
        return i;
    }

    /**
    * 归并排序 时间复杂度为O(nlogn) 空间复杂度为O(n)
    *
    * @param arrays
    */
    public static void mergeSort(int[] arrays) {
        mergeSort(arrays, 0, arrays.length - 1);
    }

    private static void mergeSort(int[] arrays, int start, int end) {
        if (start >= end) {
            return;
        }
        int mid = start + (end - start) / 2;
        mergeSort(arrays, start, mid);
        mergeSort(arrays, mid + 1, end);
        mergeArrays(arrays, start, mid, end);
    }

    /**
    * 合并
    *
    * @param arrays
    * @param start
    * @param mid
    * @param end
    */
    private static void mergeArrays(int[] arrays, int start, int mid, int end) {
        int i = start;
        int j = mid + 1;
        int k = 0;
        int[] temp = new int[end - start + 1];
        while (i <= mid && j <= end) {
            if (arrays[i] <= arrays[j]) {
                temp[k++] = arrays[i++];
            } else {
                temp[k++] = arrays[j++];
            }
        }
        int s = i;
        int e = mid;
        if (j <= end) {
            s = j;
            e = end;
        }
        while (s <= e) {
            temp[k++] = arrays[s++];
        }
        for (i = 0; i <= end - start; i++) {
            arrays[i + start] = temp[i];
        }
    }

    /**
    * 选择排序 时间复杂度O(n²) 空间复杂度O(1)
    *
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
    * 插入排序 时间复杂度O(n²) 空间复杂度O(1)
    *
    * @param arrays
    */
    public static void insertionSort(int[] arrays) {
        if (Objects.isNull(arrays) || arrays.length <= 1) {
            return;
        }
        for (int i = 1; i < arrays.length; i++) {
            int value = arrays[i];
            int j = i - 1;
            for (; j >= 0; j--) {
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
    * 冒泡排序 时间复杂度：O(n²) 空间复杂度：O(1)
    *
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
        int[] arrays = new int[] {7, 1, 3, 45, 6, 7, 8, 9, 1, 3, 2};
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

        //        System.out.print("选择排序前");
        //        print(arrays);
        //        selectionSort(arrays);
        //        System.out.print("选择排序后");
        //        print(arrays);

        //        System.out.print("归并排序前");
        //        print(arrays);
        //        mergeSort(arrays);
        //        System.out.print("归并排序后");
        //        print(arrays);

        //        System.out.print("快速排序前");
        //        print(arrays);
        //        quickSort(arrays);
        //        System.out.print("快速排序后");
        //        print(arrays);
        System.out.print("计数排序前");
        print(arrays);
        countingSort(arrays);
        System.out.print("计数排序后");
        print(arrays);
    }
}
