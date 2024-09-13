package com.mb.tutorials;

public class BinarySearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int[] arr = {1,2,3,4,5,6};
		System.out.println(binarySearch(arr, 7));
		
	}
	
	public static int binarySearch(int[] arr, int num ) {
		
		int l = 0;
		int h = arr.length -1;
		
		while(l<=h) {
			int m = (l+h)/2;
			if(num == arr[m]) {
				return m;
			}
			else if( num > arr[l]) {
				l = m+1;
			}
			else {
				h = m-1;
			}
				
		}
		return -1;
	}
	

}
