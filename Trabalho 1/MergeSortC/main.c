#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <omp.h>
#include "inc/aux.h"
#include "inc/merge_sort_sequential.h"
#include "inc/merge_sort_parallel_pt.h"
#include "inc/merge_sort_parallel_opm.h"

#define MAX_SIZE_MULTIPLIER 300
#define NUM_OF_RUNS 10

double test_sequential(int *base_array, int size)
{
    int *cloned_array = malloc(sizeof(int) * size);
    if (!cloned_array)
    {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < size; i++)
        cloned_array[i] = base_array[i];

    double start = omp_get_wtime(); // Início do tempo real
    merge_sort_sequential(cloned_array, size);
    double end = omp_get_wtime(); // Fim do tempo real

    if (!is_sorted(cloned_array, size))
    {
        printf("Erro test_sequential: o array nao esta ordenado!\n");
    }

    free(cloned_array);
    return (end - start) * 1000.0;
}

double test_parallel_pthread(int *base_array, int size)
{
    int *cloned_array = malloc(sizeof(int) * size);
    if (!cloned_array)
    {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < size; i++)
        cloned_array[i] = base_array[i];

    double start = omp_get_wtime(); // Início do tempo real
    merge_sort_parallel_pt(cloned_array, size);
    double end = omp_get_wtime(); // Fim do tempo real

    if (!is_sorted(cloned_array, size))
    {
        printf("Erro test_parallel_pthread: o array nao esta ordenado!\n");
    }

    free(cloned_array);
    return (end - start) * 1000.0;
}

double test_parallel_opm(int *base_array, int size)
{
    int *cloned_array = malloc(sizeof(int) * size);
    if (!cloned_array)
    {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < size; i++)
        cloned_array[i] = base_array[i];

    double start = omp_get_wtime(); // Início do tempo real
    merge_sort_parallel_opm(cloned_array, size);
    double end = omp_get_wtime(); // Fim do tempo real

    if (!is_sorted(cloned_array, size))
    {
        printf("Erro test_parallel_opm: o array nao esta ordenado!\n");
    }

    free(cloned_array);
    return (end - start) * 1000.0;
}

int main()
{
    srand(time(NULL));

    const char *csv_file = "C_merge_sort_benchmark.csv";
    FILE *fp = fopen(csv_file, "w");
    if (!fp)
    {
        perror("Failed to open CSV file");
        return EXIT_FAILURE;
    }

    fprintf(fp,
            "ArraySize,"
            "SeqAvg_ms,SeqStd_ms,"
            "PT_Avg_ms,PT_Std_ms,"
            "OMP_Avg_ms,OMP_Std_ms,"
            "PT_SpeedupAvg,PT_SpeedupStd,"
            "OMP_SpeedupAvg,OMP_SpeedupStd\n");

    printf(
        "%-18s %-22s %-28s %-28s\n",
        "Algoritmo",
        "Seq (ms)",
        "Pthreads (ms / speedup)",
        "OpenMP (ms / speedup)");

    for (int i = 1; i <= MAX_SIZE_MULTIPLIER; i++)
    {
        int size = 1000 * i;
        double seq_times[NUM_OF_RUNS];
        double par_pt_times[NUM_OF_RUNS];
        double par_opm_times[NUM_OF_RUNS];
        double speedup_pt_times[NUM_OF_RUNS];
        double speedup_opm_times[NUM_OF_RUNS];

        for (int run = 0; run < NUM_OF_RUNS; run++)
        {
            int *base_array = malloc(sizeof(int) * size);
            if (!base_array)
            {
                perror("malloc failed");
                exit(EXIT_FAILURE);
            }
            generate_random_array(base_array, size);

            seq_times[run] = test_sequential(base_array, size);
            par_pt_times[run] = test_parallel_pthread(base_array, size);
            par_opm_times[run] = test_parallel_opm(base_array, size);
            speedup_pt_times[run] = seq_times[run] / par_pt_times[run];
            speedup_opm_times[run] = seq_times[run] / par_opm_times[run];

            free(base_array);
        }

        double avg_seq = calculate_mean(seq_times, NUM_OF_RUNS);
        double avg_par_pt = calculate_mean(par_pt_times, NUM_OF_RUNS);
        double avg_par_opm = calculate_mean(par_opm_times, NUM_OF_RUNS);
        double avg_speedup_pt = calculate_mean(speedup_pt_times, NUM_OF_RUNS);
        double avg_speedup_opm = calculate_mean(speedup_opm_times, NUM_OF_RUNS);

        double std_seq = calculate_std(seq_times, NUM_OF_RUNS, avg_seq);
        double std_par_pt = calculate_std(par_pt_times, NUM_OF_RUNS, avg_par_pt);
        double std_par_opm = calculate_std(par_opm_times, NUM_OF_RUNS, avg_par_opm);
        double std_speedup_pt = calculate_std(speedup_pt_times, NUM_OF_RUNS, avg_speedup_pt);
        double std_speedup_opm = calculate_std(speedup_opm_times, NUM_OF_RUNS, avg_speedup_opm);

        fprintf(fp, "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
                size,
                avg_seq, std_seq,
                avg_par_pt, std_par_pt,
                avg_par_opm, std_par_opm,
                avg_speedup_pt, std_speedup_pt,
                avg_speedup_opm, std_speedup_opm);

        printf(
            "MergeSort | N=%d | Seq: %.3f±%.3f ms | PT: %.3f±%.3f (%.2fx) | OMP: %.3f±%.3f (%.2fx)\n",
            size,
            avg_seq, std_seq,
            avg_par_pt, std_par_pt, avg_speedup_pt,
            avg_par_opm, std_par_opm, avg_speedup_opm);
    }

    fclose(fp);
    printf("Dados salvos em: %s\n", csv_file);

    return 0;
}