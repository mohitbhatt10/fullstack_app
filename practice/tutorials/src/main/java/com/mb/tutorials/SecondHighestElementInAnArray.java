package com.mb.tutorials;

public class SecondHighestElementInAnArray {

	public static void main(String[] args) {
		
		int[] arr = {1,2,-3,2,1,4};
		
		System.out.println(findSecondHighest(arr));

	}
	
	public static int findSecondHighest(int[] arr) {
		
		int max = Integer.MIN_VALUE;
		int secMax = Integer.MIN_VALUE;
		
		for(int i = 0; i<arr.length; i++) {
			if(arr[i]> max) {
				secMax = max;
				max = arr[i];
			}
			if(arr[i] > secMax && arr[i] < max) {
				secMax = arr[i];
			}
		}
		return secMax;
	}

}
