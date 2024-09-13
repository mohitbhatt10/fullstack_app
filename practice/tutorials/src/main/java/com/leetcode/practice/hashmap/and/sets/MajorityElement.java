package com.leetcode.practice.hashmap.and.sets;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MajorityElement {

	public static void main(String[] args) {
		
		int[] nums = {3,2,3};//{2,2,1,1,1,2,2};
		MajorityElement obj = new MajorityElement();
		System.out.println(obj.majorityElement(nums));

	}

	public int majorityElement(int[] nums) {

		if (nums.length == 1) {
			return nums[0];
		}

		Map<Integer, Long> numberFrequencyMap = Arrays.stream(nums).boxed()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Optional<Map.Entry<Integer, Long>> majorityElement = numberFrequencyMap.entrySet().stream().filter(entry -> entry.getValue() > nums.length / 2).findFirst();
		
		if(majorityElement.isPresent()) {
			return majorityElement.get().getKey();
		}

		return 0;
	}
}
