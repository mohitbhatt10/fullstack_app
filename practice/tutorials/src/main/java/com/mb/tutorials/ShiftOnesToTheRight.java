package com.mb.tutorials;

import java.util.Arrays;

public class ShiftOnesToTheRight {
	
	
	public static void main(String[] args) {
		
		
		int[] arr = {1,2,3,4,1,5,1,6,7};
		
		//{2,3,4,5,6,7,1,1,1}
		int right = arr.length-1;
		
		for(int i = arr.length-1; i>=0; i--) {
			
			if(arr[i] == 1) {
				int temp = arr[i];
				arr[i] = arr[right];
				arr[right] = temp;
				right--;
			}
			
			System.out.println(Arrays.toString(arr));
		}
		
	}
	

}
