package com.leetcode.practice.heaps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;


/*
 * 
 * Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order.
Example 1:

Input: nums = [1,1,1,2,2,3], k = 2
Output: [1,2]
Example 2:

Input: nums = [1], k = 1
Output: [1]
 

Constraints:

1 <= nums.length <= 105
-104 <= nums[i] <= 104
k is in the range [1, the number of unique elements in the array].
It is guaranteed that the answer is unique.
 

Follow up: Your algorithm's time complexity must be better than O(n log n), where n is the array's size.
 */

public class TopKFrequentElements {
	
	public static void main(String[] args) {
		int[] nums = {1,1,1,2,2,3};
		int k = 2;
		
		System.out.println(Arrays.toString(topKFrequent1(nums, k)));
	}

	public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : nums) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }
        
        PriorityQueue<Map.Entry<Integer, Integer>> heap = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.getValue(), b.getValue())
        );
        
        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (heap.size() < k) {
                heap.offer(entry);
            } else {
                heap.offer(entry);
                heap.poll();
            }
        }
        
        int[] topK = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            topK[i] = heap.poll().getKey();
        }
        
        return topK;
    }
	
	public static int[] topKFrequent1(int[] nums, int k) {
        
		Map<Integer, Integer> frequencyMap = Arrays.stream(nums).mapToObj(obj -> (int)obj)
		.collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(count-> 1)));	
		
		List<Integer> keys = frequencyMap.entrySet().stream()
				.sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
				.limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

		return keys.stream().mapToInt(Integer::intValue).toArray();
    }
}
