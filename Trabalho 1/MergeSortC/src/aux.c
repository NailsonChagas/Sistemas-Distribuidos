#include "aux.h"
#include <stdlib.h>
#include <math.h>

void generate_random_array(int *array, int size) {
    for (int i = 0; i < size; i++) {
        array[i] = rand() % ARRAY_MAX_VALUE;
    }
}

double calculate_mean(double *values, int n) {
    double sum = 0.0;
    for (int i = 0; i < n; i++)
        sum += values[i];
    return sum / n;
}

double calculate_std(double *values, int n, double mean) {
    double sum_sq = 0.0;
    for (int i = 0; i < n; i++) {
        double diff = values[i] - mean;
        sum_sq += diff * diff;
    }
    return sqrt(sum_sq / n);
}

int is_sorted(int *array, int size) {
    for (int i = 0; i < size - 1; i++) {
        if (array[i] > array[i + 1]) {
            return 0;
        }
    }
    return 1;
}