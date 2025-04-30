package com.mb.tutorials.sorting;

public class MergeSort {

    // Function to merge two subarrays of arr[].
    // First subarray is arr[l..m], second subarray is arr[m+1..r]
    public static void merge(int[] arr, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1; // Left side array size 
        int n2 = r - m;     // Right side array size

        // Create temporary arrays
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        // Copy data to temporary arrays
        for (int i = 0; i < n1; ++i)
        	leftArray[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            rightArray[j] = arr[m + 1 + j];

        // Merge the temporary arrays

        // Initial indices of the first and second subarrays
		int i = 0;
		int j = 0;

        // Initial index of merged subarray array
        int k = l;
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                arr[k] = leftArray[i];
                i++;
            } else {
                arr[k] = rightArray[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of LEFT[] if any
        while (i < n1) {
            arr[k] = leftArray[i];
            i++;
            k++;
        }

        // Copy remaining elements of RIGHT[] if any
        while (j < n2) {
            arr[k] = rightArray[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using merge()
    public static void mergeSort(int[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Recursively sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    // Function to print the array
    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; ++i)
            System.out.print(arr[i] + " ");
        System.out.println();
    }
    public static void main(String[] args) {
        // Test array
        int[] arr = {12, 11, 13, 5, 6, 7};

        // Print original array
        System.out.println("Original array:");
        printArray(arr);

        // Perform Merge Sort
        mergeSort(arr, 0, arr.length - 1);

        // Print sorted array
        System.out.println("Sorted array:");
        printArray(arr);
    }
}
