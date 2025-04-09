package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Subsets {

	public static void main(String[] args) {
		
		int[] nums = {1,3,5};
		
		List<List<Integer>> result = generateSubsetsBackTracking(nums);
		
		for(int i =0 ;i< result.size() ;i++) {
			System.out.println(result.get(i));
		}

	}
	
	public static List<List<Integer>> subsets(int[] nums) {
		
		List<List<Integer>> result = new ArrayList<>();
		
		
		result.add(new ArrayList<>());
		
		for(int num : nums) {
			int currentSize = result.size();
            for (int i = 0; i < currentSize; i++) {
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
		}
		
		return result;
        
    }
	
	public static List<List<Integer>> generateSubsets(int[] input) {
        List<List<Integer>> result = new ArrayList<>();
        int n = input.length;
        int totalSubsets = 1 << n; // Equivalent to 2^n
        
        for (int i = 0; i < totalSubsets; i++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                // Check if j-th bit is set in i
                if ((i & (1 << j)) != 0) {
                    subset.add(input[j]);
                }
            }
            result.add(subset);
        }
        return result;
    }
	
	public static List<List<Integer>> generateSubsetsBackTracking(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums,0, new ArrayList<>(), result);
        return result;
    }
	
	public static void backtrack(int[] nums, int start, List<Integer> sol, List<List<Integer>> res) {
        res.add(new ArrayList<>(sol));
        for (int i = start; i < nums.length; i++) {
            sol.add(nums[i]);
            backtrack(nums, i + 1, sol, res); // Recursion with start = i+1, and temp. intermittent solution.
            sol.remove(sol.size() - 1); // backtracking
        }
    }

}
