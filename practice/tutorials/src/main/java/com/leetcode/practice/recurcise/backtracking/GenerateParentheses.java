package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.List;

public class GenerateParentheses {

	public static void main(String[] args) {
		
		int n = 5;
		
		GenerateParentheses obj = new GenerateParentheses();
		
		System.out.println(obj.generateParenthesis(n));

	}
	
	public List<String> generateParenthesis(int n) {
        
		List<String> result = new ArrayList<>();
		
		backtrack(n, 0, 0, new StringBuilder(), result);
		
		return result;
    }
	
	public void backtrack(int n, int open, int close, StringBuilder solution, List<String> result) {
		
		if(solution.length() == 2*n) {
			result.add(solution.toString());
			return;
		}
		
		if(open < n) {
			solution.append('(');
			backtrack(n, open+1, close, solution, result);
			solution.deleteCharAt(solution.length()-1);
		}
		
		if(close < open) {
			solution.append(')');
			backtrack(n, open, close+1, solution, result);
			solution.deleteCharAt(solution.length()-1);
		}
		
	}

}
