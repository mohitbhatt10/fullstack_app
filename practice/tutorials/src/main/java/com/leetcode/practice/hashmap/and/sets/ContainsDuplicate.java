package com.leetcode.practice.hashmap.and.sets;

import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicate {

	public static void main(String[] args) {
		
		int[] nums = {1,2,3,4};
		ContainsDuplicate obj = new ContainsDuplicate();
		System.out.println(obj.containsDuplicate(nums));

	}

	public boolean containsDuplicate(int[] nums) {
		
		Set<Integer> set = new HashSet<>();
		
		for(Integer num : nums) {
			if(set.contains(num)) {
				return true;
			}
			set.add(num);
		}
		return false;
		
	}

}
