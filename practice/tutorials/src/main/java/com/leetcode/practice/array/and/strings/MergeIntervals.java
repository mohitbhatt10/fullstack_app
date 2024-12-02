package com.leetcode.practice.array.and.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/*
 * Given an array of intervals where intervals[i] = [starti, endi], 
 * merge all overlapping intervals, and return an array of the non-overlapping intervals
 * that cover all the intervals in the input.

 

Example 1:

Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]
Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
Example 2:

Input: intervals = [[1,4],[4,5]]
Output: [[1,5]]
Explanation: Intervals [1,4] and [4,5] are considered overlapping.
 */
public class MergeIntervals {

	public static void main(String[] args) {
		
		int[][] intervals = {{1,3},{2,6},{8,10},{15,18}};
		
		MergeIntervals obj = new MergeIntervals();
		Arrays.stream(obj.merge(intervals)).forEach(arr -> System.out.println(Arrays.toString(arr)));

	}

	public int[][] merge(int[][] intervals) {
		
		if(intervals.length<= 1) {
			return intervals;
		}
		
		//sort the interval array based on the first element of interval
		Arrays.sort(intervals, Comparator.comparingInt(eachArray -> eachArray[0]));
		
		//Adding the first sorted interval as it is because here we start with the merging
		int[] boundary = intervals[0];
		List<int[]> result = new ArrayList<>();
		result.add(boundary);
		
		//starting with the second array interval till the last one.
		for(int i=1 ;i< intervals.length ;i++) {
			
			//if the high boundary of first interval is greater than low boundary of adjacent intervals 
			//then update the previous interval to merge in both intervals. 
			if(intervals[i][0] <= boundary[1]) {
				boundary[1] = Math.max(intervals[i][1], boundary[1]);
			}
			// update the boundary with next interval because there is no intersection
			// and add in the final list.
			else {
				boundary = intervals[i];
				result.add(boundary);
			}
		}
		
		
		return result.toArray(new int[result.size()][]);
	}
}
