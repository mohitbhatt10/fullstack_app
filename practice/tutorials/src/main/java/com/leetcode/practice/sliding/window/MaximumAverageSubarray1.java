package com.leetcode.practice.sliding.window;

public class MaximumAverageSubarray1 {

	public static void main(String[] args) {
		
		int[] nums = {1,12,-5,-6,50,3};////			
		int k = 4;
		
		System.out.println(findMaxAverage(nums, k));


	}
	
	public static double findMaxAverageBruteForce(int[] nums, int k) {
        
		int left = 0;
		int right = k-1;
		int n = nums.length;
		double maxAverage = -10001;
		
		while(right<n) {
			double temptotal = 0;
			for(int i=left;i<=right;i++) {
				temptotal += nums[i];
			}
			maxAverage = Math.max(maxAverage, temptotal/k);
			left++;
			right++;
		}
		return maxAverage;
    }
	
	public static double findMaxAverage(int[] nums, int k) {
        
		int left = 0;
		int right = k-1;
		int n = nums.length;
		double temptotal = 0;
		
		for(int i=0; i<=right; i++) {
			temptotal += nums[i];
		}
		
		double maxAverage =  temptotal/k;
		
		left++;
		right++;
		
		while(right<n) {
			temptotal = temptotal - nums[left-1]+ nums[right];
			maxAverage = Math.max(maxAverage, temptotal/k);
			left++;
			right++;
		}
		return maxAverage;
    }

}
