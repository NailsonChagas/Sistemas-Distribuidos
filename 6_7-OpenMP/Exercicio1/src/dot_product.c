#include "dot_product.h"
#include <omp.h>

double dot_product_seq(const double *a, const double *b, int lenght) {
    double result = 0.0;

    for (int i = 0; i < lenght; i++) {
        result += a[i] * b[i];
    }

    return result;
}

double dot_product_omp(const double *a, const double *b, int lenght) {
    double result = 0.0;

    #pragma omp parallel for reduction(+:result)
    for (int i = 0; i < lenght; i++) {
        result += a[i] * b[i];
    }

    return result;
}