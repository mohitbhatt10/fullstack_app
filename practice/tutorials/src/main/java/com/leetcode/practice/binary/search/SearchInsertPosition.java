package com.leetcode.practice.binary.search;

public class SearchInsertPosition {

	public static void main(String[] args) {
		
		int[] nums = {1,3,5,6};
		int target = 2;
		System.out.println(searchInsert(nums, target));
		

	}
	
	public static int searchInsert(int[] nums, int target) {
		int l = 0;
		int h = nums.length-1;
		
		
		
		while(l<=h) {
			int m = (l+h)/2;
			if(nums[m] == target) {
				return m;
			}
			else if(nums[m]> target) {
				h = m-1;
			}
			else {
				l = m+1;
			}
		}
		return l;
    }
}
