import java.io.FileWriter;
import java.io.PrintWriter;
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

    private static long test_parallel(int[] base_array) {
        int[] cloned_array = base_array.clone();
        MergeSortParallel merge_sort = new MergeSortParallel(cloned_array);

        long start_time = System.nanoTime();
        merge_sort.start();
        try {
            merge_sort.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end_time = System.nanoTime();

        return end_time - start_time;
    }

    private static double calculateStd(double[] values, double mean) {
        double sumSq = 0;
        for (double v : values) {
            double diff = v - mean;
            sumSq += diff * diff;
        }
        return Math.sqrt(sumSq / values.length);
    }

    public static void main(String[] args) {
        int max_size_multiplier = 150;
        int num_of_runs = 25;
        Random rand = new Random();

        String csvFile = "merge_sort_benchmark.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {

            // Cabeçalho CSV
            String headerCSV = "ArraySize,SeqAvg_ms,SeqStd_ms,ParAvg_ms,ParStd_ms,SpeedupAvg,SpeedupStd";
            writer.println(headerCSV);

            // Cabeçalho Console (formatado)
            System.out.printf("%-10s %-12s %-12s %-12s %-12s %-12s %-12s%n",
                    "ArraySize", "SeqAvg(ms)", "SeqStd(ms)", "ParAvg(ms)", "ParStd(ms)", "SpeedupAvg", "SpeedupStd");

            for (int i = 1; i <= max_size_multiplier; i++) {
                int size = 1000 * i;

                long[] seq_times = new long[num_of_runs];
                long[] par_times = new long[num_of_runs];
                double[] speedup_times = new double[num_of_runs];

                for (int run = 0; run < num_of_runs; run++) {
                    int[] base_array = new int[size];
                    for (int index = 0; index < size; index++) {
                        base_array[index] = rand.nextInt(100_000);
                    }

                    seq_times[run] = test_sequential(base_array);
                    par_times[run] = test_parallel(base_array);

                    speedup_times[run] = (double) seq_times[run] / par_times[run];
                }

                // Média e desvio
                double avg_seq = 0, avg_par = 0, avg_speedup = 0;
                for (long t : seq_times) avg_seq += t / 1_000_000.0;
                for (long t : par_times) avg_par += t / 1_000_000.0;
                for (double s : speedup_times) avg_speedup += s;

                avg_seq /= num_of_runs;
                avg_par /= num_of_runs;
                avg_speedup /= num_of_runs;

                double std_seq = calculateStd(
                        java.util.Arrays.stream(seq_times).mapToDouble(t -> t / 1_000_000.0).toArray(),
                        avg_seq
                );
                double std_par = calculateStd(
                        java.util.Arrays.stream(par_times).mapToDouble(t -> t / 1_000_000.0).toArray(),
                        avg_par
                );
                double std_speedup = calculateStd(speedup_times, avg_speedup);

                String lineCSV = String.format("%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f",
                        size, avg_seq, std_seq, avg_par, std_par, avg_speedup, std_speedup);

                writer.println(lineCSV);

                System.out.printf(
                        "MergeSort -> Arr size: %d / Seq Avg: %.3f ms ± %.3f / Par Avg: %.3f ms ± %.3f / Speedup Avg: %.3f ± %.3f\n",
                        size, avg_seq, std_seq, avg_par, std_par, avg_speedup, std_speedup
                );
            }

            System.out.println("Dados salvos em: " + csvFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}