package com.leetcode.practice.two.pointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreeSum {

	public static void main(String[] args) {
		int[] nums = {3,0,-2,-1,1,2};
		ThreeSum obj = new ThreeSum();
		System.out.println(obj.threeSum(nums));

	}

	public List<List<Integer>> threeSum(int[] nums) {
		
		Set<List<Integer>> result = new HashSet<>();
		Arrays.sort(nums);
		
		for(int i=0;i<nums.length-2;i++) {
			
			int j = i+1;
			int k = nums.length-1;
			
			while(j<k) {
				int sum = nums[i]+nums[j]+nums[k];
				if(sum == 0) {
					result.add(Arrays.asList(nums[i],nums[j],nums[k]));
					j++;
					k--;
				}
				else if(sum<0) {
					j++;
				}
				else {
					k--;
				}
			}
		}	
		return new ArrayList<>(result);
	}
}
