public class MergeSortParallel extends Thread {
    private static int min_size_to_thread = 0; // se eu deixar sem isso fica mais lento por criar um monte de thread
    private int[] array;
    private int start_index;
    private int end_index;

    public MergeSortParallel(int[] array, int min_size_to_thread) { //construtor publico
        this(array, 0, array.length - 1);
        MergeSortParallel.min_size_to_thread = min_size_to_thread;
    }

    private MergeSortParallel(int[] array, int start, int end) { // construtor usado para o mergeSort
        this.array = array;
        this.start_index = start;
        this.end_index = end;
    }

    @Override
    public void run() {
        mergeSort(start_index, end_index);
    }

    private void mergeSort(int start, int end) {
        if (start >= end) return;

        int mid_index = (start + end) / 2;

        if (end - start < min_size_to_thread) {
             /*
             tamanho do array é pequeno demais para criar nova thread -> executar sequencialmente
             */
            mergeSort(start, mid_index);
            mergeSort(mid_index + 1, end);
        } else {
            /*
            tamanho do array é grande o bastante para criar nova thread -> executar paralelamente
             */

            MergeSortParallel left = new MergeSortParallel(array, start, mid_index);
            MergeSortParallel right = new MergeSortParallel(array, mid_index + 1, end);

            right.start();
            left.start();

            try {
                right.join();
                left.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
