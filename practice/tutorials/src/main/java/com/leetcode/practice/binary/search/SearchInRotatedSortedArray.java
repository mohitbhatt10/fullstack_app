package com.leetcode.practice.binary.search;


/*
 * There is an integer array nums sorted in ascending order (with distinct values).

Prior to being passed to your function, nums is possibly rotated at an unknown pivot index k (1 <= k < nums.length) such that the resulting array is [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]] (0-indexed). For example, [0,1,2,4,5,6,7] might be rotated at pivot index 3 and become [4,5,6,7,0,1,2].

Given the array nums after the possible rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.

You must write an algorithm with O(log n) runtime complexity.
 
Example 1:

Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4
Example 2:

Input: nums = [4,5,6,7,0,1,2], target = 3
Output: -1
Example 3:

Input: nums = [1], target = 0
Output: -1
 */
public class SearchInRotatedSortedArray {

	public static void main(String[] args) {
		
		int[] nums = {3,1}; 
		int target = 3;
		
		System.out.println(search(nums, target));

	}
	
	public static  int search(int[] nums, int target) {
		
		int n = nums.length;
		int l = 0;
		int h = n -1;
		
		// First find the min-index same as MinIndex problem in rotated sorted array 
		while(l < h) {
			int m = l +(h-l)/2;
			
			if(nums[m]> nums[h]) {
				l = m+1;
			}
			else {
				h = m;
			}
			
		}
		
		int midIndex = l; // it could be h also as both are same after the above loop
		
		// Now there are 3 cases.
		// 1. when array is already sorted after rotation means we have to perform normal binary search where l=0 and h=n-1
		// 2. the target lies in left sub-array from the pivot(minIndex) then discard the right sub-array from minIndex to n-1
		// 3. the target lies in right sub-array from the pivot, then discard the left sub-array from 0 to minIndex
		if(midIndex == 0) {
			l=0;
			h=n-1;
		}
		else if(nums[0] <= target && target<=nums[midIndex-1]) {
			l = 0;
			h = midIndex-1;
		}
		else {
			l= midIndex;
			h = n-1;
		}
		
		//perform the normal binary search when we know the updated boundary for the target.
		while(l<=h) {
			int m = l + (h-l)/2;
			
			if(nums[m] == target) {
				return m;
			}
			else if(nums[m] < target){
				l = m+1;
			}
			else {
				h = m-1;
			}
		}
		
		return -1;
    }

}
