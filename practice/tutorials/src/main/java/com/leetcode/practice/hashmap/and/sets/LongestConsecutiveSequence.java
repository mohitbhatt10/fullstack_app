package com.leetcode.practice.hashmap.and.sets;

import java.util.HashMap;
import java.util.Map;

public class LongestConsecutiveSequence {

	public static void main(String[] args) {
		int[] nums = {100,4,200,1,3,2};
		LongestConsecutiveSequence obj = new LongestConsecutiveSequence();
		System.out.println(obj.longestConsecutive(nums));
	}

	public int longestConsecutive(int[] nums) {
		
		int longestSequence = 0;
		Map<Integer,Boolean> occorenceMap = new HashMap<>();
		
		
		for(int i=0;i<nums.length;i++) {
			occorenceMap.put(nums[i],Boolean.FALSE);
		}
		
		for(int i=0;i< nums.length;i++) {
			
			int currentSequence = 1;
			
			int nextNum = nums[i]+1;
			while(occorenceMap.containsKey(nextNum) && occorenceMap.get(nextNum) == Boolean.FALSE) {
				currentSequence++;
				occorenceMap.put(nextNum, Boolean.TRUE);
				nextNum++;
			}
			
			int previousNum = nums[i]-1;
			while(occorenceMap.containsKey(previousNum) && occorenceMap.get(previousNum) == Boolean.FALSE) {
				currentSequence++;
				occorenceMap.put(previousNum, Boolean.TRUE);
				previousNum--;
			}
			
			longestSequence = Math.max(longestSequence, currentSequence);
		}
		
		return longestSequence;
		
	}
}
