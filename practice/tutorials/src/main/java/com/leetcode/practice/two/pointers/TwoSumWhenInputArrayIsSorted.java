package com.leetcode.practice.two.pointers;

import java.util.Arrays;

public class TwoSumWhenInputArrayIsSorted {

	public static void main(String[] args) {
		int []numbers = {2,7,11,15};
		int target = 9;
		TwoSumWhenInputArrayIsSorted obj = new TwoSumWhenInputArrayIsSorted();
		System.out.println(Arrays.toString(obj.twoSum(numbers, target)));
	}
	
	public int[] twoSum(int[] nums, int target) {
		
		int n = nums.length;
		int l = 0;
		int r = n-1; 
		
		
		while(l<r) {
			if(nums[l]+nums[r]==target) {
				return new int[] {l+1,r+1};
			}
			else if(nums[l]+nums[r] > target) {
				r--;
			}
			else {
				l++;
			}
		}
		return null;
	}
}
