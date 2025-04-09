package com.leetcode.practice.heaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/*
 * Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane and an integer k, return the k closest points to the origin (0, 0).

The distance between two points on the X-Y plane is the Euclidean distance (i.e., √(x1 - x2)2 + (y1 - y2)2).

You may return the answer in any order. The answer is guaranteed to be unique (except for the order that it is in).

 

Example 1:


Input: points = [[1,3],[-2,2]], k = 1
Output: [[-2,2]]
Explanation:
The distance between (1, 3) and the origin is sqrt(10).
The distance between (-2, 2) and the origin is sqrt(8).
Since sqrt(8) < sqrt(10), (-2, 2) is closer to the origin.
We only want the closest k = 1 points from the origin, so the answer is just [[-2,2]].
Example 2:

Input: points = [[3,3],[5,-1],[-2,4]], k = 2
Output: [[3,3],[-2,4]]
Explanation: The answer [[-2,4],[3,3]] would also be accepted.
 

Constraints:

1 <= k <= points.length <= 104
-104 <= xi, yi <= 104
 * 
 * 
 */
public class KClosestPointsTOrigin {

	public static void main(String[] args) {

		int[][] points = {{2,2},{2,2},{3,3},{2,-2},{1,1}};//{{3,3},{5,-1},{-2,4}};//{{1,3},{-2,2}};
		int k = 4;
		
		int[][] matrix = kClosest(points, k);

        // Print the matrix
        for (int[] row : matrix) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
	}
	
	public static int[][] kClosest(int[][] points, int k) {
        // Custom comparator to sort based on distance
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
            int distA = a[0] * a[0] + a[1] * a[1];
            int distB = b[0] * b[0] + b[1] * b[1];
            return Integer.compare(distB, distA); // Max-heap
        });

        for (int[] point : points) {
            maxHeap.add(point);
            if (maxHeap.size() > k) {
                maxHeap.poll(); // Remove the farthest point
            }
        }

        // Convert the heap to an array
        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }

        return result;
    }
	
	public static int[][] kClosest1(int[][] points, int k) {
        
		
		Map<List<Integer>, Double> distances = new HashMap<>();
		
		for(int i= 0 ;i< points.length; i++) {
			int[] point = points[i];
			distances.put(Arrays.stream(point).boxed().collect(Collectors.toList()),
					Math.sqrt(Math.pow(point[0], 2) +Math.pow(point[1],2)));
		}
		
		List<List<Integer>> collect = distances.entrySet().stream()
		.sorted(Map.Entry.<List<Integer>, Double>comparingByValue())
		.limit(k)
		.map(Map.Entry::getKey)
        .collect(Collectors.toList());
		
		return convertToMatrix(collect);
    }
	
	private static int[][] convertToMatrix(List<List<Integer>> listOfLists) {
        int rows = listOfLists.size();
        int cols = listOfLists.get(0).size(); // Assuming all rows have the same size

        int[][] matrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = listOfLists.get(i).get(j); // Convert Integer to int
            }
        }

        return matrix;
    }

}
