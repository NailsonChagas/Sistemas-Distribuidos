#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <omp.h>
#include "inc/aux.h"
#include "inc/dot_product.h"

#define MAX_SIZE_MULTIPLIER 600
#define NUM_OF_RUNS 10

double test_sequential(double *a, double *b, int size)
{
    double start = omp_get_wtime();
    dot_product_seq(a, b, size);
    double end = omp_get_wtime();

    return (end - start) * 1000.0;
}

double test_parallel(double *a, double *b, int size)
{
    double start = omp_get_wtime();
    dot_product_omp(a, b, size);
    double end = omp_get_wtime();

    return (end - start) * 1000.0;
}

int main()
{
    omp_set_num_threads(omp_get_num_procs());

    srand(time(NULL));

    const char *csv_file = "C_dot_product_benchmark.csv";
    FILE *fp = fopen(csv_file, "w");
    if (!fp)
    {
        perror("Failed to open CSV file");
        return EXIT_FAILURE;
    }

    fprintf(fp,
            "ArraySize,"
            "SeqAvg_ms,SeqStd_ms,"
            "OMP_Avg_ms,OMP_Std_ms,"
            "OMP_SpeedupAvg,OMP_SpeedupStd\n");

    printf(
        "%-18s %-22s %-28s\n",
        "Algoritmo",
        "Seq (ms)",
        "OpenMP (ms / speedup)");

    for (int i = 1; i <= MAX_SIZE_MULTIPLIER; i++)
    {
        int size = 10000 * i; 

        double seq_times[NUM_OF_RUNS];
        double omp_times[NUM_OF_RUNS];
        double speedup_times[NUM_OF_RUNS];

        for (int run = 0; run < NUM_OF_RUNS; run++)
        {
            double *a = malloc(sizeof(double) * size);
            double *b = malloc(sizeof(double) * size);

            if (!a || !b)
            {
                perror("malloc failed");
                exit(EXIT_FAILURE);
            }

            generate_random_array(a, size);
            generate_random_array(b, size);

            seq_times[run] = test_sequential(a, b, size);
            omp_times[run] = test_parallel(a, b, size);
            speedup_times[run] = seq_times[run] / omp_times[run];

            free(a);
            free(b);
        }

        double avg_seq = calculate_mean(seq_times, NUM_OF_RUNS);
        double avg_omp = calculate_mean(omp_times, NUM_OF_RUNS);
        double avg_speedup = calculate_mean(speedup_times, NUM_OF_RUNS);

        double std_seq = calculate_std(seq_times, NUM_OF_RUNS, avg_seq);
        double std_omp = calculate_std(omp_times, NUM_OF_RUNS, avg_omp);
        double std_speedup = calculate_std(speedup_times, NUM_OF_RUNS, avg_speedup);

        fprintf(fp, "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
                size,
                avg_seq, std_seq,
                avg_omp, std_omp,
                avg_speedup, std_speedup);

        printf(
            "DotProduct | N=%d | Seq: %.3f±%.3f ms | OMP: %.3f±%.3f (%.2fx)\n",
            size,
            avg_seq, std_seq,
            avg_omp, std_omp, avg_speedup);
    }

    fclose(fp);
    printf("Dados salvos em: %s\n", csv_file);

    return 0;
}