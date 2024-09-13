package com.leetcode.practice.array.and.strings;

import java.util.ArrayList;
import java.util.List;

public class SummaryRanges {

	public static void main(String[] args) {
		int[] arr = {0,2,3,4,6,8,9};//{0,1,2,4,5,7};
		SummaryRanges obj = new SummaryRanges();
		System.out.println(obj.summaryRanges(arr));

	}
	
	public List<String> summaryRanges(int[] nums) {
		
		List<String> ranges = new ArrayList<>();
		
		int firstIndex = 0;
		int lastIndex = 0;
		
		for(int i=0; i<nums.length ;i++) {
			if(i+1<nums.length && nums[i+1] == nums[i]+1) {
				lastIndex++;
			}
			else {
				if(lastIndex == firstIndex) {
					ranges.add(String.valueOf(nums[lastIndex]));
				}
				else {
					ranges.add(nums[firstIndex]+"->"+nums[lastIndex]);
				}
				firstIndex = lastIndex+1;
				lastIndex ++;
			}
		}
        return ranges;
    }

}
