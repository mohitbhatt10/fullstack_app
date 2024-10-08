package com.mb.tutorials.sorting;

import java.util.Arrays;

public class BubbleSort {

	public static void main(String[] args) {
		
		int[] arr = {5,5,4,2,1};
		System.out.println(Arrays.toString(bubbleSort(arr)));

	}
	
	public static int[] bubbleSort(int[] arr) {
		
		if(arr == null || arr.length == 1) {
			return arr;
		}
		
		int n = arr.length;
		
		for(int i = 0; i< n-1 ;i++) {
			for(int j = i+1; j< n ;j++) {
				if(arr[i]>arr[j]) {
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
		
		return arr;
	}
}
