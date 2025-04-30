package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Given an array of distinct integers candidates and a target integer target, return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.

The same number may be chosen from candidates an unlimited number of times. Two combinations are unique if the frequency of at least one of the chosen numbers is different.

The test cases are generated such that the number of unique combinations that sum up to target is less than 150 combinations for the given input.

 

Example 1:

Input: candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]
Explanation:
2 and 3 are candidates, and 2 + 2 + 3 = 7. Note that 2 can be used multiple times.
7 is a candidate, and 7 = 7.
These are the only two combinations.
Example 2:

Input: candidates = [2,3,5], target = 8
Output: [[2,2,2,2],[2,3,3],[3,5]]
Example 3:

Input: candidates = [2], target = 1
Output: []
 
 * 
 * 
 */

public class CombinationSum {
	
	Set<List<Integer>> uniqueSolution = new HashSet<>();

	public static void main(String[] args) {
		int[] candidates = {2,3,6,7};
		int target = 7;
		
		CombinationSum obj = new CombinationSum();
		
		System.out.println(obj.combinationSum(candidates, target));

	}
	
	public List<List<Integer>> combinationSum(int[] candidates, int target) {
        
		List<List<Integer>> result = new ArrayList<>();
		List<Integer> solution = new ArrayList<>();
		

		backTrack(candidates, 0, target, result, solution);
		
		return result;
		
    }

	
	public void backTrack(int[] candidates, int index, int target, List<List<Integer>> result, List<Integer> solution) {
		
		if(index == candidates.length || target < 0) {
			return;
		}
		
		if(target == 0) {
			if(uniqueSolution.add(solution))
				result.add(new ArrayList<>(solution));
			return;
		}
		
		solution.add(candidates[index]);
		
		backTrack(candidates, index+1, target-candidates[index], result, solution); // single
		backTrack(candidates, index, target-candidates[index], result, solution); // multiple
		
		solution.remove(solution.size() -1); // backtrack
		backTrack(candidates, index+1, target, result, solution); // exclude
	
	}
}
