package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Given an array nums of distinct integers, return all the possible permutations. You can return the answer in any order.

 

Example 1:

Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
Example 2:

Input: nums = [0,1]
Output: [[0,1],[1,0]]
Example 3:

Input: nums = [1]
Output: [[1]]
 */

public class Permutations {

	public static void main(String[] args) {
		int[] nums = {1,2,3};
		
		System.out.println(permute(nums));
	}
	
	public static List<List<Integer>> permute(int[] nums) {
        
		
		int n = nums.length;
		List<List<Integer>> result = new ArrayList<>(); // stores the result recursively.
		List<Integer> solution = new ArrayList<>(); // Intermittent variable to store each permutation.
		boolean[] isPresent = new boolean[n]; // boolean array to check whether that position already considered in the permutation.
		
		backtrack(nums, result, solution, isPresent);
		
		return result;
    }
	
	public static void backtrack(int[] nums, List<List<Integer>> result, List<Integer> solution,boolean[] isPresent) {
		
		
		// Base case: when intermittent solution length is equal to length of nums array  
		//            then simply add the copy of that solution and close/get out of recursive stack.
		if(solution.size() == nums.length) {
			result.add(new ArrayList<>(solution));
			return;
		}
		
		//Other case: loop it from 0 to nums length
		//            if that postion's number already present then continue
		//            else add that number in solution, mark present true, call recursively
		//                 backtrack
		//                 mark that number not present after back tracking.
		for(int i =0 ;i< nums.length; i++) {
			if(isPresent[i]) {
				continue;
			}
			solution.add(nums[i]);
			isPresent[i] = true;
			backtrack(nums, result, solution, isPresent);
			solution.remove(solution.size() -1); // backtrack
			isPresent[i] = false;
		}
	}

}
