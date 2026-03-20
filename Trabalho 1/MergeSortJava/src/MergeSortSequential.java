public class MergeSortSequential {

    private static final int INSERTION_SORT_THRESHOLD = 64;

    public static void sort(int[] arr) {
        int[] tmp = new int[arr.length];
        msort(arr, tmp, 0, arr.length - 1);
    }

    private static void msort(int[] arr, int[] tmp, int l, int r) {
        int size = r - l + 1;

        if (size <= INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, l, r);
            return;
        }

        int m = (l + r) / 2;

        msort(arr, tmp, l, m);
        msort(arr, tmp, m + 1, r);

        merge(arr, tmp, l, m, r);
    }

    private static void insertionSort(int[] arr, int l, int r) {
        for (int i = l + 1; i <= r; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= l && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void merge(int[] arr, int[] tmp, int l, int m, int r) {
        int i = l, j = m + 1, k = l;

        while (i <= m && j <= r) {
            tmp[k++] = (arr[i] <= arr[j]) ? arr[i++] : arr[j++];
        }

        while (i <= m) tmp[k++] = arr[i++];
        while (j <= r) tmp[k++] = arr[j++];

        System.arraycopy(tmp, l, arr, l, r - l + 1);
    }
}