package com.leetcode.practice.binary.search;

public class BinarySearch {

	public static void main(String[] args) {
		int[] nums = {5};
		int target = 5;
		System.out.println(search(nums, target));

	}
	
	public static int search(int[] nums, int target) {
        
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
		return -1;
    }
}
