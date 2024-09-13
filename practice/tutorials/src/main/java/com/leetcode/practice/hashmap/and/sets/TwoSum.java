package com.leetcode.practice.hashmap.and.sets;

import java.util.*;

public class TwoSum {

	public static void main(String[] args) {
		
		int[] nums = {2,7,11,15};
		TwoSum obj = new TwoSum();
		System.out.println(Arrays.toString(obj.twoSum(nums, 13)));

	}
	
	public int[] twoSum(int[] nums, int target) {
       
		Map<Integer,Integer> map = new HashMap<>();

		for(int i = 0;i<nums.length;i++) {
			int diff = target - nums[i];
			if(map.containsKey(diff)) {
				return new int[]{map.get(diff),i};
			}
			map.put(nums[i],i );
		}
		return new int[] {};
    }

}
