package com.mb.tutorials.sorting;

public class InsertionSort {

	public static void main(String[] args) {
        // Test array
        int[] arr = {12, 11, 13, 5, 6};
        
        // Print original array
        System.out.println("Original array:");
        printArray(arr);
        
        // Perform Insertion Sort
        insertionSort(arr);
        
        // Print sorted array
        System.out.println("Sorted array:");
        printArray(arr);
    }

	// Function to perform Insertion Sort on an array
    public static void insertionSort(int[] arr) {
        int n = arr.length;

        // Start from the second element as the first element is already "sorted"
        for (int i = 1; i < n; i++) {
            int key = arr[i];  // The current element to be inserted
            int j = i - 1;

            // Move elements of the sorted portion that are greater than 'key'
            // one position ahead to make space for the key
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }

            // Insert the key at its correct position in the sorted portion
            arr[j + 1] = key;
        }
    }
    
 // Function to print the array
    public static void printArray(int[] arr) {
        for (int i : arr) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
