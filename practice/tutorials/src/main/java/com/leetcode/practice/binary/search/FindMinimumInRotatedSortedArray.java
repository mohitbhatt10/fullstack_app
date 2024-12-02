package com.leetcode.practice.binary.search;

public class FindMinimumInRotatedSortedArray {

	public static void main(String[] args) {
		
		int[] nums = {3,4,5,1,2};
		System.out.println(findMin(nums));
		

	}

	public static int findMin(int[] nums) {
        
		int n = nums.length;
		int l = 0;
		int h = n-1;
		
		while(l< h) { // break the loop if both l are h are equal
			int m = l + (h -l)/2;
			
			if(nums[m]>nums[h]) { // if the right most element is lesser than middle element then ignore the first half of array 
				l = m+1;
			}
			else { // if right most element is greater of equal then middle element (nums[m]<= h) then ignore the second half
				h = m;
			}
		}
		return nums[l]; // we can return nums[h] as both are same.
    }
}
