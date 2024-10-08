package com.leetcode.practice.binary.search;

public class SearchA2dMatrix {

	public static void main(String[] args) {
		
		int[][] matrix = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};
		
		System.out.println(searchMatrix(matrix, 3));

	}
	
	public static boolean searchMatrix(int[][] matrix, int target) {
		
		int m = matrix.length; // 3 (no. of rows)
		int n = matrix[0].length; // 4 (no. of columns)
		int total = m*n; // total elements in matrix (12)
		
		int l = 0;
		int h = total-1; // 11
		
		while(l<=h) {
			int mid = (l+h)/2;  // (11/2 = 5)
			
			int i = mid/n;     //  (5/4 = 1) 
			int j = mid%n;     //  (5%4 = 1)
			
			if(target == matrix[i][j]) {  // 13 != 11
				return true;
			}
			else if(target> matrix[i][j]){  // true then make l = 5+1 = 6 and re-process the loop
				l = mid+1;
			}
			else {
				h = mid -1;   
			}
		}
		
		return false;
    }

}
