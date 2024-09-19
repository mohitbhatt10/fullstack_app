package com.leetcode.practice.stacks;

import java.util.Arrays;
import java.util.Stack;

public class DailyTemperatures {

	public static void main(String[] args) {
		
		int[] temperatures = {73,74,75,71,69,72,76,73};
		DailyTemperatures obj = new DailyTemperatures();
		System.out.println(Arrays.toString(obj.dailyTemperatures(temperatures)));
		

	}
	
	public int[] dailyTemperatures(int[] temperatures) {
        
		int n = temperatures.length;
		int[] result = new int[n];
		Stack<Integer> stack = new Stack<>();
		
		for(int i=n-1; i>=0;i--) {
			
			// If stack is not empty then remove/pop all the elements which are smaller
			//than current element
			while(!stack.isEmpty() && temperatures[i] >= temperatures[stack.peek()]) {
				stack.pop();
			}
			
			//By removing indexes which are smaller that current one,
			//then only greater indexes element would remain in the stack
			// then just find the difference between the peek element and current element's indexes
			if(!stack.isEmpty()) {
				result[i] = stack.peek() - i;
			}
			
			//push the current index so we can say this much is already traversed.
			stack.push(i);
		}
		
		return result;
		
    }

}
