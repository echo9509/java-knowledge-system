package search;

public class Search {

    public static int bsearch(int[] array, int val) {
        return bsearch(array, 0, array.length, val);
    }

    private static int bsearch(int[] array, int low, int high, int val) {
        if (low > high || low < 0 || high >= array.length) {
            return -1;
        }
        int mid = low + ((high - low) >> 1);
        if (array[mid] == val) {
            return mid;
        } else if (array[mid] < val) {
            return bsearch(array, low, mid - 1, val);
        } else {
            return bsearch(array, mid + 1, high, val);
        }
    }

    public static void main(String[] args) {
        int[] arrays = new int[]{
          0,1,3,5,7,8,9,10
        };
        System.out.println(bsearch(arrays,-7));
    }
}
