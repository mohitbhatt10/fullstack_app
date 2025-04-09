package com.leetcode.practice.sliding.window;

/*
 * Given a binary array nums and an integer k,
 * return the maximum number of consecutive 1's in the array if you can flip at most k 0's.

Example 1:

Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
Output: 6
Explanation: [1,1,1,0,0,1,1,1,1,1,1]
Bolded numbers were flipped from 0 to 1. The longest subarray is underlined.
Example 2:

Input: nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3
Output: 10
Explanation: [0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1]
Bolded numbers were flipped from 0 to 1. The longest subarray is underlined.
 * 
 * 
*/

public class MaxConsecutiveOnes {

	public static void main(String[] args) {
		
		int [] nums = {1,1,1,0,0,0,1,1,1,1,0};
		int k = 2;
		
		System.out.println(longestOnes(nums, k));
	}
	
	
	/*
	 * Good Example of sliding window algo where we define the boundary/window within array to find the 
	 * Desired output. Asked in many coding interviews. 
	 */
	public static int longestOnes(int[] nums, int k) {
        int start = 0; // Left bound
        int maxWindowSize = 0;  // counter to count the max window size of consecutive 1s after swapping with max k 0s.
        int zeros = 0; // counter to count the zero till ith position in array within window (end-start+1)
        
        //Right bound what will increase after every iteration
        for(int end=0 ; end< nums.length ; end++) {
        	
        	// if ith element is 0 then increase the 0s counter
        	if(nums[end] == 0) {
        		zeros++;
        	}
        	
        	//if 0s counter gets greater than k(desired one) than 
        	// 1. Reduce 0s till it becomes k 
        	// 2. Increase left bound(start) every time whether its 0 or 1 until 0s count becomes k
        	while(zeros > k) {
        		if(nums[start] == 0) {
        			zeros --;
        		}
        		start++;
        	}
        	
        	//Finally the maxWindowCount would be the maximum of current window vs Max window till now in the iteration
        	maxWindowSize = Math.max(maxWindowSize, end-start+1);
        	
        }
        return maxWindowSize;
    }
	
	int longestOnes2(int[] nums, int k) {

	    int zeroCount = 0;
	    int start = 0;
	    int max_ones = 0;

	    for (int end = 0; end < nums.length; end++) {
	      if (nums[end] == 0)
	        zeroCount++;

	      while (zeroCount > k) {
	        if (nums[start] == 0)
	          zeroCount--;

	        start++;
	      }

	      max_ones = Math.max(max_ones, end - start + 1);
	    }
	    return max_ones;
	  }

}
