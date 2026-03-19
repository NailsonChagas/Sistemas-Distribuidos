import java.util.Random;

/*
Array size: 10
Sequential avg: 0.000 ms
Parallel avg:   1.108 ms
Sequential is faster
-----------------------------
Array size: 100
Sequential avg: 0.001 ms
Parallel avg:   1.176 ms
Sequential is faster
-----------------------------
Array size: 1000
Sequential avg: 0.001 ms
Parallel avg:   1.088 ms
Sequential is faster
-----------------------------
Array size: 10000
Sequential avg: 0.002 ms
Parallel avg:   1.167 ms
Sequential is faster
-----------------------------
Array size: 50000
Sequential avg: 0.007 ms
Parallel avg:   1.027 ms
Sequential is faster
-----------------------------
Array size: 100000
Sequential avg: 0.012 ms
Parallel avg:   0.959 ms
Sequential is faster
-----------------------------
Array size: 1000000
Sequential avg: 0.177 ms
Parallel avg:   1.224 ms
Sequential is faster
-----------------------------
Array size: 2000000
Sequential avg: 0.206 ms
Parallel avg:   1.289 ms
Sequential is faster
-----------------------------
Array size: 3000000
Sequential avg: 0.433 ms
Parallel avg:   1.360 ms
Sequential is faster
-----------------------------
Array size: 4000000
Sequential avg: 0.654 ms
Parallel avg:   1.503 ms
Sequential is faster
-----------------------------
Array size: 5000000
Sequential avg: 0.556 ms
Parallel avg:   1.333 ms
Sequential is faster
-----------------------------
Array size: 6000000
Sequential avg: 1.211 ms
Parallel avg:   1.672 ms
Sequential is faster
-----------------------------
Array size: 7000000
Sequential avg: 0.842 ms
Parallel avg:   1.533 ms
Sequential is faster
-----------------------------
Array size: 8000000
Sequential avg: 1.340 ms
Parallel avg:   1.532 ms
Sequential is faster
-----------------------------
Array size: 9000000
Sequential avg: 1.702 ms
Parallel avg:   1.952 ms
Sequential is faster
-----------------------------
Array size: 10000000
Sequential avg: 1.163 ms
Parallel avg:   1.633 ms
Sequential is faster
-----------------------------
Array size: 50000000
Sequential avg: 9.588 ms
Parallel avg:   4.932 ms
Parallel is faster
-----------------------------
Array size: 100000000
Sequential avg: 12.192 ms
Parallel avg:   9.111 ms
Parallel is faster
-----------------------------
Array size: 500000000
Sequential avg: 76.012 ms
Parallel avg:   45.129 ms
Parallel is faster
-----------------------------
Array size: 1000000000
Sequential avg: 201.270 ms
Parallel avg:   73.895 ms
Parallel is faster
-----------------------------
*/

public class Main {

    private static long test_sequential(int[] arr, int target, int expected_index) {
        long start = System.nanoTime();
        int result = LinearSearch.sequentialSearch(arr, target);
        long end = System.nanoTime();

        if (result == -1) {
            throw new RuntimeException("Sequential failed: element not found");
        }

        if (result != expected_index) { // só para checar se implementei  certo
            throw new RuntimeException("Sequential failed: wrong index");
        }

        return end - start;
    }

    private static long test_parallel(int[] arr, int target, int expected_index) {
        long start = System.nanoTime();
        int result = LinearSearch.parallelSearch(arr, target);
        long end = System.nanoTime();

        if (result == -1) {
            throw new RuntimeException("Parallel failed: element not found");
        }

        if (result != expected_index) {
            throw new RuntimeException("Parallel failed: wrong index");
        }

        return end - start;
    }

    // Fisher-Yates shuffle (O(n))
    private static void shuffle(int[] arr, Random rand) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void main(String[] args) {
        int[] sizes = {
                10, 100, 1000, 10_000, 50_000,
                100_000, 1_000_000, 2_000_000, 3_000_000,
                4_000_000, 5_000_000, 6_000_000, 7_000_000,
                8_000_000, 9_000_000, 10_000_000, 50_000_000,
                100_000_000, 500_000_000, 1_000_000_000
        };
        int repetitions = 15;

        Random rand = new Random();

        // WARMUP -> problema que tive com o exercicio 1
        int[] warmup_array = new int[100_000];
        for (int i = 0; i < warmup_array.length; i++) {
            warmup_array[i] = i;
        }
        shuffle(warmup_array, rand);

        int warmup_target = warmup_array[rand.nextInt(warmup_array.length)];

        for (int i = 0; i < 5; i++) {
            LinearSearch.sequentialSearch(warmup_array, warmup_target);
            LinearSearch.parallelSearch(warmup_array, warmup_target);
        }

        for (int size : sizes) {
            long total_seq = 0;
            long total_par = 0;

            // valores unicos
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = i;
            }

            for (int r = 0; r < repetitions; r++) {
                // embaralha
                shuffle(arr, rand);

                // pega um indice aleatorio
                int random_index = rand.nextInt(size);
                int target = arr[random_index];

                total_seq += test_sequential(arr, target, random_index);
                total_par += test_parallel(arr, target, random_index);
            }

            double avg_seq = total_seq / (double) repetitions / 1_000_000.0;
            double avg_par = total_par / (double) repetitions / 1_000_000.0;

            System.out.println("Array size: " + size);
            System.out.printf("Sequential avg: %.3f ms\n", avg_seq);
            System.out.printf("Parallel avg:   %.3f ms\n", avg_par);
            System.out.println((avg_seq < avg_par) ? "Sequential is faster" : "Parallel is faster");
            System.out.println("-----------------------------");
        }
    }
}