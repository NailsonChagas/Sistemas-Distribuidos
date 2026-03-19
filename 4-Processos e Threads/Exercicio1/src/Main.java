/*
Resultado dos testes (CPU AMD® Ryzen 5 2600 six-core processor × 12)
int array_size = 1_000_000;

available_cores: 12
array_size: 1000000

sequential | avg_time_ms = 143.28079259999998 ms
min_size = 666666 | avg_time_ms = 93.673802 ms
min_size = 333333 | avg_time_ms = 61.855078799999994 ms
min_size = 166666 | avg_time_ms = 52.3108648 ms
min_size = 83333 | avg_time_ms = 34.5367254 ms
min_size = 41666 | avg_time_ms = 29.854012 ms
min_size = 20833 | avg_time_ms = 35.3719183 ms
min_size = 10416 | avg_time_ms = 43.7349212 ms
min_size = 5208 | avg_time_ms = 69.4768033 ms
min_size = 2604 | avg_time_ms = 121.64776 ms
min_size = 1302 | avg_time_ms = 245.1469021 ms
min_size = 651 | avg_time_ms = 508.5411114 ms
min_size = 325 | avg_time_ms = 1219.9308179000002 ms
 */

import java.util.Random;

public class Main {

    private static long test_sequential(int[] base_array) {
        int[] cloned_array = base_array.clone();

        MergeSortSequential merge_sort = new MergeSortSequential(cloned_array);

        long start_time = System.nanoTime();

        merge_sort.sort();

        long end_time = System.nanoTime();

        return end_time - start_time;
    }

    private static long test_parallel(int[] base_array, int min_size) {
        int[] cloned_array = base_array.clone();

        MergeSortParallel merge_sort = new MergeSortParallel(cloned_array, min_size);

        long start_time = System.nanoTime();

        merge_sort.start();
        try {
            merge_sort.join();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        long end_time = System.nanoTime();

        return end_time - start_time;
    }

    public static void main(String[] args) {

        int available_cores = Runtime.getRuntime().availableProcessors();
        int array_size = 1_000_000;

        int[] base_array = new int[array_size];

        Random random = new Random();

        // generate base array
        for (int index = 0; index < array_size; index++) {
            base_array[index] = random.nextInt(100000);
        }

        int[] test_values = {
                (int) (array_size / (available_cores * 0.125)),
                (int) (array_size / (available_cores * 0.25)),
                (int) (array_size / (available_cores * 0.5)),
                array_size / (available_cores * 1), // -> 1 thread por nucleo
                array_size / (available_cores * 2),
                array_size / (available_cores * 4),
                array_size / (available_cores * 8),
                array_size / (available_cores * 16),
                array_size / (available_cores * 32),
                array_size / (available_cores * 64),
                array_size / (available_cores * 128),
                array_size / (available_cores * 256)
        };
        /*
        existe um limite de quanto criar threads ajuda a resolver o problema, depois de certo ponto o overhead deixa mais
        lento que o puramente sequencial
         */

        System.out.println("available_cores: " + available_cores);
        System.out.println("array_size: " + array_size);
        System.out.println();

        /*
        se eu n faço isso antes de começar o teste o primeiro caso de teste paralelo sempre sera MUITO mais lento (aprox 70ms a mais)
        sei lá o que ta rolando aqui kkkkkk
         */
        for (int run = 0; run < 10; run++) {
            test_parallel(base_array, array_size / available_cores);
        }

        long total_sequential_time = 0;
        int number_of_runs = 10;

        for (int run = 0; run < number_of_runs; run++) {
            total_sequential_time += test_sequential(base_array);
        }

        double average_sequential_time_ms =
                (total_sequential_time / (double) number_of_runs) / 1_000_000.0;

        System.out.println("> sequential | avg_time_ms = " + average_sequential_time_ms + " ms");

        for (int min_size : test_values) {
            long total_parallel_time = 0;

            for (int run = 0; run < number_of_runs; run++) {
                total_parallel_time += test_parallel(base_array, min_size);
            }

            double average_parallel_time_ms =
                    (total_parallel_time / (double) number_of_runs) / 1_000_000.0;

            System.out.println("min_size = " + min_size +
                    " | avg_time_ms = " + average_parallel_time_ms + " ms");
        }
    }
}