package com.leetcode.practice.array.and.strings;

import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {

	public static void main(String[] args) {

		int[][] matrix = { { 1, 2, 3 }, 
				           { 4, 5, 6 }, 
				           { 7, 8, 9 } 
				         };

		SpiralMatrix obj = new SpiralMatrix();
		System.out.println(obj.spiralOrder(matrix));
	}

	public List<Integer> spiralOrder(int[][] matrix) {

		List<Integer> spiralOrder = new ArrayList<>();

		int rowMin = 0;
		int rowMax = matrix.length - 1;
		int colMin = 0;
		int colMax = matrix[0].length - 1;

		while (rowMin <= rowMax && colMin <= colMax) {

			// left to right (row is constant, column vary incremental, then increase
			// rowMin)
			for (int i = colMin; i <= colMax; i++) {
				spiralOrder.add(matrix[rowMin][i]);
			}
			rowMin++;

			// top to down (column is constant, row vary incremental, then decrease colMax)
			for (int i = rowMin; i <= rowMax; i++) {
				spiralOrder.add(matrix[i][colMax]);

			}
			colMax--;

			// right to left (row is constant, column vary decremental, then decrement
			// rowMax)
			if(rowMin <= rowMax) {
				for (int i = colMax; i >= colMin; i--) {
					spiralOrder.add(matrix[rowMax][i]);
				}
			}
			rowMax--;

			// down to top (column is constant, row vary decremental, then increase colMin)
			if(colMin <= colMax) {
				for (int i = rowMax; i >= rowMin; i--) {
					spiralOrder.add(matrix[i][colMin]);
				}
			}
			colMin++;

		}

		return spiralOrder;

	}

}
