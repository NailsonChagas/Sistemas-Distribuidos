// https://www.geeksforgeeks.org/dsa/merge-sort/

public class MergeSortSequential {

    private int[] array;

    public MergeSortSequential(int[] array) {
        this.array = array;
    }

    public void sort() {
        merge_sort(0, array.length - 1);
    }

    private void merge_sort(int start, int end) {
        if (start >= end) return;

        int mid_index = (start + end) / 2;

        merge_sort(start, mid_index);
        merge_sort(mid_index + 1, end);

        merge(start, mid_index, end);
    }

    private void merge(int start, int mid, int end) {
        int[] temp = new int[end - start + 1];

        int i = start;
        int j = mid + 1;
        int k = 0;

        while (i <= mid && j <= end) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= mid) temp[k++] = array[i++];
        while (j <= end) temp[k++] = array[j++];

        System.arraycopy(temp, 0, array, start, temp.length);
    }
}