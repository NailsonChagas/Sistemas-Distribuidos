#ifndef AUX_H
#define AUX_H

#define ARRAY_MAX_VALUE 100000

void generate_random_array(int *array, int size);
double calculate_mean(double *values, int n);
double calculate_std(double *values, int n, double mean);

#endif