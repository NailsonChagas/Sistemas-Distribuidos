#include "merge_sort_sequential.h"
#include <stdlib.h>
#include <stdio.h>

static void merge(int *array, int start, int mid, int end) {
    int n = end - start + 1;
    int *temp = (int *)malloc(n * sizeof(int));
    if (!temp) {
        perror("malloc failed");
        exit(EXIT_FAILURE);
    }

    int i = start, j = mid + 1, k = 0;
    while (i <= mid && j <= end) {
        temp[k++] = (array[i] <= array[j]) ? array[i++] : array[j++];
    }

    while (i <= mid) temp[k++] = array[i++];
    while (j <= end) temp[k++] = array[j++];

    for (i = 0; i < n; i++)
        array[start + i] = temp[i];

    free(temp);
}

static void merge_sort_recursive(int *array, int start, int end) {
    if (start >= end) return;

    int mid = (start + end) / 2;
    merge_sort_recursive(array, start, mid);
    merge_sort_recursive(array, mid + 1, end);
    merge(array, start, mid, end);
}

void merge_sort_sequential(int *array, int size) {
    merge_sort_recursive(array, 0, size - 1);
}