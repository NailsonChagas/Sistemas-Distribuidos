#include "merge_sort_parallel_pt.h"
#include <pthread.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define INSERTION_SORT_THRESHOLD 64

typedef struct
{
    int *arr;
    int *tmp;
    int l;
    int r;
    int depth;
} thread_args_t;

// insertion sort
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

// merge com buffer auxiliar
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

// forward declaration
static void msort_pt(int *arr, int *tmp, int l, int r, int depth);

// wrapper para thread
static void *thread_func(void *arg)
{
    thread_args_t *args = (thread_args_t *)arg;
    msort_pt(args->arr, args->tmp, args->l, args->r, args->depth);
    return NULL;
}

static void msort_pt(int *arr, int *tmp, int l, int r, int depth)
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
        pthread_t t1, t2;

        thread_args_t left = {arr, tmp, l, m, depth - 1};
        thread_args_t right = {arr, tmp, m + 1, r, depth - 1};

        pthread_create(&t1, NULL, thread_func, &left);
        pthread_create(&t2, NULL, thread_func, &right);

        pthread_join(t1, NULL);
        pthread_join(t2, NULL);
    }
    else
    {
        msort_pt(arr, tmp, l, m, 0);
        msort_pt(arr, tmp, m + 1, r, 0);
    }

    merge(arr, tmp, l, m, r);
}

void merge_sort_parallel_pt(int *arr, int n)
{
    int *tmp = malloc(n * sizeof(int));
    if (!tmp)
        return;

    int num_cores = sysconf(_SC_NPROCESSORS_ONLN);

    int max_depth = 0;
    while ((1 << max_depth) < num_cores)
        max_depth++;

    msort_pt(arr, tmp, 0, n - 1, max_depth);

    free(tmp);
}