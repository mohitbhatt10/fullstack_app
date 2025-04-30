package com.leetcode.practice.dynamic.programming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Given an integer array nums, return the length of the longest strictly increasing subsequence.

 

Example 1:

Input: nums = [10,9,2,5,3,7,101,18]
Output: 4
Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.
Example 2:

Input: nums = [0,1,0,3,2,3]
Output: 4
Example 3:

Input: nums = [7,7,7,7,7,7,7]
Output: 1
 

Constraints:

1 <= nums.length <= 2500
-104 <= nums[i] <= 104
 

Follow up: Can you come up with an algorithm that runs in O(n log(n)) time complexity?
 * 
 */

public class LongestIncreasingSubsequence {

	public static void main(String[] args) {
		int[] nums = {0,1,0,3,2,3};//{10,9,2,5,3,7,101,18};
		
		LongestIncreasingSubsequence obj = new LongestIncreasingSubsequence();
		System.out.println(obj.lengthOfLIS(nums));
	}
	
	public int lengthOfLISMy(int[] nums) {
		
		int n = nums.length;
		List<Integer> dp = new ArrayList<>(n);
		
		for(int i =0 ;i< n ;i++)
		{
			dp.add(1);
		}
		 
		for(int i=1 ; i< n; i++) {
			int maxLIS = 1;
			int currLIS = 1;
			for(int j=0; j<i ;j++) {
				if(nums[i]> nums[j]) {
					currLIS = dp.get(j)+1;
					maxLIS = Math.max(maxLIS, currLIS);
				}
			}
			dp.set(i,maxLIS);
		}
		return Collections.max(dp);
        
    }
	
	public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, 1);

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        int maxLength = 0;
        for (int length : dp) {
            maxLength = Math.max(maxLength, length);
        }

        return maxLength;
    }

}
