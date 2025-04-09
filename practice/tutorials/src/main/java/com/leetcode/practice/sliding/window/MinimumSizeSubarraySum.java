package com.leetcode.practice.sliding.window;

public class MinimumSizeSubarraySum {

	public static void main(String[] args) {
		
		int target = 7;
		int[] nums = {2,3,1,2,4,3};
		System.out.println(minSubArrayLen(target, nums));

	}
	
	public static int minSubArrayLen(int target, int[] nums) {
        int start = 0; // Left bound
        int minLength = Integer.MAX_VALUE; // Initialize the minLength to Max integer
        int sum = 0; // Initialize sum to 0
        
        
        // Increase the Right bound(end) to every iteration
        for(int end=0; end< nums.length; end++) {
        	
        	// Add up right bound to the sum in every iteration
        	sum += nums[end];
        	
        	// If sum gets larger than the target then calculate the minimumLength and subtract the left bound and increase the position of left bound.
        	while(sum >= target) {
        		int windowLength = end - start + 1; // current windowLenght
        		minLength = Math.min(minLength, windowLength); // current min length
        		sum -= nums[start]; // subtract the left bound
        		start++; // increase the position on left bound.
        	}
        	
        	
        }
        // If any combination of sum becomes >= target then minLenght would be updated else it would remain Max integer so need to return 0 instead.
        return minLength == Integer.MAX_VALUE ? 0 : minLength; 
    }

}
