package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.List;

public class Combinations {

	public static void main(String[] args) {
		
		System.out.println(combine(4, 2));
	}
	
	public static List<List<Integer>> combine(int n, int k) {
		List<List<Integer>> result = new ArrayList<>();
		List<Integer> solution = new ArrayList<>();
		backtrack(n, k, result , solution);
		
		return result;
    }
	
	public static void backtrack(int x, int k, List<List<Integer>> result, List<Integer> solution) {
		
		
		// Base case: when intermittent solution length is equal to k (no. of elements in combination)  
		//            then simply add the copy of that solution and close/get out of recursive stack.
		if(solution.size() == k) {
			result.add(new ArrayList<>(solution));
			return;
		}
		
		if(x > k - solution.size()) {
			backtrack(x-1, k, result , solution);
		}
		
		solution.add(x);
		backtrack(x-1, k, result , solution);
		solution.remove(solution.size() -1); // backtrack
	}
}
