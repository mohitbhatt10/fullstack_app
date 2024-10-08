package com.mb.tutorials.sorting;

import java.util.Arrays;
/*
 * Selection Sort Overview:

Selection Sort is an in-place comparison sorting algorithm.
It divides the input array into two parts: the sorted part and the unsorted part.
In each iteration, 
it selects the smallest element from the unsorted part and swaps it with
the first element of the unsorted part.
 */
public class SelectionSort {

	public static void main(String[] args) {
		
		int[] arr = {5,5,3,2,1};
		
		System.out.println(Arrays.toString(selectionSort(arr)));

	}
	
	public static int[] selectionSort(int[] arr) {
		
		if(arr == null || arr.length == 1) {
			return arr;
		}
		
		int n = arr.length;
		
		for(int i= 0 ;i<n-1 ;i++) {
			int min = Integer.MIN_VALUE;
			int minIndex = i;
			for(int j = i+1; j<n; j++) {
				if(arr[i] > arr[j] && arr[i]> min ) {
					min = arr[j];
					minIndex = j;
				}
			}
			int temp = arr[i];
			arr[i] = arr[minIndex];
			arr[minIndex] = temp; 
			
		}
		return arr;
	}
	
	// Function to sort an array using Selection Sort
    public static void selectionSortBetterApproach(int[] arr) {
        int n = arr.length;
        
        // Traverse the array
        for (int i = 0; i < n - 1; i++) {
            // Assume the first unsorted element is the minimum
            int minIndex = i;
            
            // Find the minimum element in the unsorted portion
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j; // Update minIndex if smaller element is found
                }
            }
            
            // Swap the found minimum element with the first unsorted element
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
}
