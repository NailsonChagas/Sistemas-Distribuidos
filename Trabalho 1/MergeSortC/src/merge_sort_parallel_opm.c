#include <omp.h>
#include <stdlib.h>
#include <string.h>
#include "merge_sort_parallel_opm.h"

#define INSERTION_SORT_THRESHOLD 64

// insertion sort para pequenos blocos (mais que recursão)
static inline void insertion_sort(int *arr, int l, int r)
{
    for (int i = l + 1; i <= r; i++)
    {
        int key = arr[i];
        int j = i - 1;
        while (j >= l && arr[j] > key)
        {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

// merge usando buffer auxiliar (SEM malloc)
static inline void merge(int *arr, int *tmp, int l, int m, int r)
{
    int i = l, j = m + 1, k = l;

    while (i <= m && j <= r)
    {
        tmp[k++] = (arr[i] <= arr[j]) ? arr[i++] : arr[j++];
    }

    while (i <= m)
        tmp[k++] = arr[i++];
    while (j <= r)
        tmp[k++] = arr[j++];

    memcpy(arr + l, tmp + l, (r - l + 1) * sizeof(int));
}

void msort(int *arr, int *tmp, int l, int r, int depth)
{
    int size = r - l + 1;

    if (size <= INSERTION_SORT_THRESHOLD)
    {
        insertion_sort(arr, l, r);
        return;
    }

    int m = (l + r) >> 1;

    if (depth > 0)
    {
#pragma omp task shared(arr, tmp)
        msort(arr, tmp, l, m, depth - 1);

#pragma omp task shared(arr, tmp)
        msort(arr, tmp, m + 1, r, depth - 1);

#pragma omp taskwait
    }
    else
    {
        msort(arr, tmp, l, m, 0);
        msort(arr, tmp, m + 1, r, 0);
    }

    merge(arr, tmp, l, m, r);
}

void merge_sort_parallel_opm(int *arr, int n)
{
    int *tmp = malloc(n * sizeof(int));

    int max_threads = omp_get_max_threads();
    int max_depth = 0;

    // calcula profundidade ideal (log2 threads)
    while ((1 << max_depth) < max_threads)
        max_depth++;

#pragma omp parallel
    {
#pragma omp single nowait
        msort(arr, tmp, 0, n - 1, max_depth);
    }

    free(tmp);
}