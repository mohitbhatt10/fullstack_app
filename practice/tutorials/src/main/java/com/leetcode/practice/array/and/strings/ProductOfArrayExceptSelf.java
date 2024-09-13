package com.leetcode.practice.array.and.strings;

import java.util.Arrays;

/*
 * Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].

The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.

You must write an algorithm that runs in O(n) time and without using the division operation.

Example 1:

Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Example 2:

Input: nums = [-1,1,0,-3,3]
Output: [0,0,9,0,0]
*/

public class ProductOfArrayExceptSelf {

	public static void main(String[] args) {

		int[] nums = {0,0};//{1,2,3,4};//{ -1, 1, 0, -3, 3 };// {1,2,3,4};
		ProductOfArrayExceptSelf obj = new ProductOfArrayExceptSelf();
		System.out.println(Arrays.toString(obj.productExceptSelf(nums)));

	}

	public int[] productExceptSelfOld(int[] nums) {

		long allProduct = 1l;
		int zeroCount = 0;
		int[] arr = new int[nums.length];

		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != 0) {
				allProduct = allProduct * nums[i];
			} else {
				zeroCount++;
			}
		}
		if (zeroCount > 0) {
			if (zeroCount >= nums.length-1 && nums.length >= 3 ) {
				for (int i = 0; i < nums.length; i++) {
					arr[i] = 0;
				}
			} else {
				for (int i = 0; i < nums.length; i++) {
					if (nums[i] != 0) {
						arr[i] = 0;
					} else {
						arr[i] = (int) allProduct;
					}
				}
			}
		} else {
			for (int i = 0; i < nums.length; i++) {
				arr[i] = (int) (allProduct / nums[i]);
			}
		}
		return arr;
	}
	
	
	public int[] productExceptSelf(int[] nums) {
		
		int[] leftArr = new int[nums.length];
		int[] rightArr = new int[nums.length];
		int[] finalArr = new int[nums.length];
		
		leftArr[0] = 1;
		rightArr[nums.length-1] = 1;
		
		int leftCounter = 1;
		int rightCounter = 1;
		
		for(int i = 0, j= nums.length-1; i< nums.length-1 && j>0 ; i++ , j--) {
			
			leftCounter *= nums[i];
			rightCounter *= nums[j];
			leftArr[i+1] = leftCounter;
			rightArr[j-1] = rightCounter;
		}
		
		for(int i=0;i<nums.length;i++) {
			finalArr[i] = leftArr[i] * rightArr[i];
		}
		
		
		return finalArr;
	}
}
