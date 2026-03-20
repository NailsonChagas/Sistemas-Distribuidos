import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {

    private static final int MAX_SIZE_MULTIPLIER = 150;
    private static final int NUM_OF_RUNS = 10;

    public static double testSequential(int[] baseArray) {
        int[] cloned = baseArray.clone();

        long start = System.nanoTime();
        MergeSortSequential.sort(cloned);
        long end = System.nanoTime();

        return (end - start) / 1_000_000.0;
    }

    public static double testParallel(int[] baseArray) {
        int[] cloned = baseArray.clone();

        long start = System.nanoTime();
        MergeSortParallel.sort(cloned);
        long end = System.nanoTime();

        return (end - start) / 1_000_000.0;
    }

    public static void main(String[] args) throws IOException {
        Random rand = new Random();

        FileWriter writer = new FileWriter("java_merge_sort_benchmark.csv");

        writer.write(
                "ArraySize," +
                        "SeqAvg_ms,SeqStd_ms," +
                        "ParAvg_ms,ParStd_ms," +
                        "SpeedupAvg,SpeedupStd\n"
        );

        System.out.printf(
                "%-18s %-22s %-28s\n",
                "Algoritmo",
                "Seq (ms)",
                "Parallel (ms / speedup)"
        );

        for (int i = 1; i <= MAX_SIZE_MULTIPLIER; i++) {
            int size = 1000 * i;

            double[] seqTimes = new double[NUM_OF_RUNS];
            double[] parTimes = new double[NUM_OF_RUNS];
            double[] speedups = new double[NUM_OF_RUNS];

            for (int run = 0; run < NUM_OF_RUNS; run++) {
                int[] baseArray = new int[size];
                for (int j = 0; j < size; j++) {
                    baseArray[j] = rand.nextInt();
                }

                seqTimes[run] = testSequential(baseArray);
                parTimes[run] = testParallel(baseArray);
                speedups[run] = seqTimes[run] / parTimes[run];
            }

            double avgSeq = mean(seqTimes);
            double avgPar = mean(parTimes);
            double avgSpeed = mean(speedups);

            double stdSeq = std(seqTimes, avgSeq);
            double stdPar = std(parTimes, avgPar);
            double stdSpeed = std(speedups, avgSpeed);

            writer.write(String.format(
                    "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
                    size,
                    avgSeq, stdSeq,
                    avgPar, stdPar,
                    avgSpeed, stdSpeed
            ));

            System.out.printf(
                    "MergeSort | N=%d | Seq: %.3f±%.3f ms | Par: %.3f±%.3f (%.2fx)\n",
                    size,
                    avgSeq, stdSeq,
                    avgPar, stdPar, avgSpeed
            );
        }

        writer.close();
        System.out.println("Dados salvos em: java_merge_sort_benchmark.csv");
    }

    public static double mean(double[] arr) {
        double sum = 0;
        for (double v : arr) sum += v;
        return sum / arr.length;
    }

    public static double std(double[] arr, double mean) {
        double sum = 0;
        for (double v : arr) {
            double d = v - mean;
            sum += d * d;
        }
        return Math.sqrt(sum / arr.length);
    }
}