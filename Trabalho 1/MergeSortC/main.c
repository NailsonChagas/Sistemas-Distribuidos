#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include "inc/merge_sort_sequential.h"
#include "inc/merge_sort_parallel.h"

#define MAX_SIZE_MULTIPLIER 150
#define NUM_OF_RUNS 25
#define ARRAY_MAX_VALUE 100000

// Função para gerar array aleatório
void generate_random_array(int *array, int size) {
    for (int i = 0; i < size; i++) {
        array[i] = rand() % ARRAY_MAX_VALUE;
    }
}

// Função para calcular média de um array de doubles
double calculate_mean(double *values, int n) {
    double sum = 0.0;
    for (int i = 0; i < n; i++)
        sum += values[i];
    return sum / n;
}

// Função para calcular desvio padrão
double calculate_std(double *values, int n, double mean) {
    double sum_sq = 0.0;
    for (int i = 0; i < n; i++) {
        double diff = values[i] - mean;
        sum_sq += diff * diff;
    }
    return sqrt(sum_sq / n);
}

// Função para medir tempo da ordenação sequencial em ms
double test_sequential(int *base_array, int size) {
    int *cloned_array = malloc(sizeof(int) * size);
    if (!cloned_array) {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < size; i++) cloned_array[i] = base_array[i];

    clock_t start = clock();
    merge_sort_sequential(cloned_array, size);
    clock_t end = clock();

    free(cloned_array);
    return ((double)(end - start)) / CLOCKS_PER_SEC * 1000.0; // ms
}

// Função para medir tempo da ordenação paralela em ms
double test_parallel(int *base_array, int size) {
    int *cloned_array = malloc(sizeof(int) * size);
    if (!cloned_array) {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < size; i++) cloned_array[i] = base_array[i];

    clock_t start = clock();
    merge_sort_parallel(cloned_array, size);
    clock_t end = clock();

    free(cloned_array);
    return ((double)(end - start)) / CLOCKS_PER_SEC * 1000.0; // ms
}

int main() {
    srand(time(NULL));

    const char *csv_file = "merge_sort_benchmark.csv";
    FILE *fp = fopen(csv_file, "w");
    if (!fp) {
        perror("Failed to open CSV file");
        return EXIT_FAILURE;
    }

    // Cabeçalho CSV
    fprintf(fp, "ArraySize,SeqAvg_ms,SeqStd_ms,ParAvg_ms,ParStd_ms,SpeedupAvg,SpeedupStd\n");

    // Cabeçalho console
    printf("%-10s %-12s %-12s %-12s %-12s %-12s %-12s\n",
           "ArraySize", "SeqAvg(ms)", "SeqStd(ms)", "ParAvg(ms)", "ParStd(ms)", "SpeedupAvg", "SpeedupStd");

    for (int i = 1; i <= MAX_SIZE_MULTIPLIER; i++) {
        int size = 150 * 100000;// 1000 * i;
        double seq_times[NUM_OF_RUNS];
        double par_times[NUM_OF_RUNS];
        double speedup_times[NUM_OF_RUNS];

        for (int run = 0; run < NUM_OF_RUNS; run++) {
            int *base_array = malloc(sizeof(int) * size);
            if (!base_array) {
                perror("malloc failed");
                exit(EXIT_FAILURE);
            }
            generate_random_array(base_array, size);

            seq_times[run] = test_sequential(base_array, size);
            par_times[run] = test_parallel(base_array, size);
            speedup_times[run] = seq_times[run] / par_times[run];

            free(base_array);
        }

        double avg_seq = calculate_mean(seq_times, NUM_OF_RUNS);
        double avg_par = calculate_mean(par_times, NUM_OF_RUNS);
        double avg_speedup = calculate_mean(speedup_times, NUM_OF_RUNS);

        double std_seq = calculate_std(seq_times, NUM_OF_RUNS, avg_seq);
        double std_par = calculate_std(par_times, NUM_OF_RUNS, avg_par);
        double std_speedup = calculate_std(speedup_times, NUM_OF_RUNS, avg_speedup);

        fprintf(fp, "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
                size, avg_seq, std_seq, avg_par, std_par, avg_speedup, std_speedup);

        printf("MergeSort -> Arr size: %d / Seq Avg: %.3f ms ± %.3f / Par Avg: %.3f ms ± %.3f / Speedup Avg: %.3f ± %.3f\n",
               size, avg_seq, std_seq, avg_par, std_par, avg_speedup, std_speedup);
    }

    fclose(fp);
    printf("Dados salvos em: %s\n", csv_file);

    return 0;
}