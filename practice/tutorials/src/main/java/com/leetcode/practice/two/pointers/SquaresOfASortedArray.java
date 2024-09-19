package com.leetcode.practice.two.pointers;

import java.util.Arrays;

/*
 * Given an integer array nums sorted in non-decreasing order, 
 * return an array of the squares of each number sorted in non-decreasing order.

 	Example 1:
	
	Input: nums = [-4,-1,0,3,10]
	Output: [0,1,9,16,100]
	Explanation: After squaring, the array becomes [16,1,0,9,100].
	After sorting, it becomes [0,1,9,16,100].
 * 
 * 
 * 
 */

public class SquaresOfASortedArray {

	public static void main(String[] args) {

		int[] nums = {-4,-1,0,3,10};
		SquaresOfASortedArray obj = new SquaresOfASortedArray();
		System.out.println(Arrays.toString(obj.sortedSquares(nums)));

	}

	public int[] sortedSquares(int[] nums) {
		
		for(int i=0;i<nums.length;i++) {
			nums[i] = nums[i]*nums[i];
		}
		
		int[] resultArr = new int[nums.length];
		int left = 0;
		int right = nums.length -1;
		
		for(int i = nums.length -1 ;i >= 0 ;i--) {
			
			// Right pointer is greater
			if(nums[right]> nums[left]) {
				resultArr[i] = nums[right];
				right --;
			}
			//Left Pointer is greater
			else {
				resultArr[i] = nums[left];
				left++;
			}
		}
		
		return resultArr;
	}
}
