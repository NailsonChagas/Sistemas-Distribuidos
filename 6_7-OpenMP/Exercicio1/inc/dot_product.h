#ifndef DOT_PRODUCT_H
#define DOT_PRODUCT_H

double dot_product_seq(const double *a, const double *b, int lenght);
double dot_product_omp(const double *a, const double *b, int lenght);

#endif