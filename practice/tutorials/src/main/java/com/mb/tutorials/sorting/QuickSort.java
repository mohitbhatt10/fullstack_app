package com.mb.tutorials.sorting;

public class QuickSort {

    // Function to partition the array on the basis of pivot
    public static int partition(int[] arr, int low, int high) {
        // Choose the rightmost element as pivot
        int pivot = arr[high];
        
        // Index of smaller element
        int i = (low - 1);

        // Traverse the array and compare each element with the pivot
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;  // Increment index of smaller element
                // Swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;  // Return the partitioning index
    }

    // Function to perform QuickSort
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // Find pivot element such that
            // elements smaller than pivot are on the left
            // elements greater than pivot are on the right
            int pi = partition(arr, low, high);

            // Recursively sort elements before and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Function to print the array
    public static void printArray(int[] arr) {
        for (int i : arr) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Test array
        int[] arr = {10, 80, 30, 90, 40, 50, 70};
        
        // Print original array
        System.out.println("Original array:");
        printArray(arr);
        
        // Perform Quick Sort
        quickSort(arr, 0, arr.length - 1);
        
        // Print sorted array
        System.out.println("Sorted array:");
        printArray(arr);
    }
}
