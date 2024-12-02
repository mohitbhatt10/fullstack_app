package com.leetcode.practice.array.and.strings;

import java.util.Arrays;

public class RotateImage {
	
	public static void main(String[] args) {
		int[][] matrix = { { 1, 2, 3 }, 
		           		   { 4, 5, 6 }, 
		                   { 7, 8, 9 } 
		         };

		RotateImage obj = new RotateImage();
		obj.rotate(matrix);
	}

	public void rotate(int[][] matrix) {
		
		
		int rows = matrix.length;
		int columns = matrix[0].length;
		
		//transpose: means all i becomes j and all j becomes i
		for(int i =0;i<rows;i++) {
			for(int j=i+1 ; j< columns; j++) {
				int temp = matrix[i][j];
				matrix[i][j] = matrix[j][i];
				matrix[j][i] = temp;
			}
		}
		
		//reflection: means take the vertical mirror image from middle column of array
		for(int i= 0 ;i< rows;i++) {
			for(int j = 0;j < rows/2 ;j++) {
				int temp = matrix[i][j];
				matrix[i][j] = matrix[i][rows-j-1];
				matrix[i][rows-j-1] = temp;
			}
		}
		
		for(int i =0 ;i<rows;i++) {
			System.out.println(Arrays.toString(matrix[i]));
		}
		
	}
}
