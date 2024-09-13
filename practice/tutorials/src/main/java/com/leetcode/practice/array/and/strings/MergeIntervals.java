package com.leetcode.practice.array.and.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
		
		Arrays.sort(intervals, Comparator.comparingInt(eachArray -> eachArray[0]));
		
		int[] boundary = intervals[0];
		List<int[]> result = new ArrayList<>();
		result.add(boundary);
		
		for(int i=1 ;i< intervals.length ;i++) {
			if(intervals[i][0] <= boundary[1]) {
				boundary[1] = Math.max(intervals[i][1], boundary[1]);
			}
			else {
				boundary = intervals[i];
				result.add(boundary);
			}
		}
		
		
		return result.toArray(new int[result.size()][]);
	}
}
