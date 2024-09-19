package com.leetcode.practice.stacks;

import java.util.ArrayDeque;
import java.util.Stack;

public class BaseballGame {

	public static void main(String[] args) {
		
		String[] ops = {"5","2","C","D","+"};
		BaseballGame obj = new BaseballGame();
		System.out.println(obj.calPoints(ops));

	}
	
	public int calPoints(String[] operations) {
	
		int totalPoints = 0;
		ArrayDeque<Integer> stack = new ArrayDeque <>();
		
		for(int i=0;i< operations.length;i++) {
			String current = operations[i];
			if(isNumeric(current)) {
				stack.add(Integer.parseInt(current));
			}
			else if("+".equals(current)) {
				int last = stack.removeLast();
				int secondLast = stack.peekLast();
				int sum = last+secondLast;
				stack.add(last);
				stack.add(sum);
			}
			else if("C".equals(current)) {
				stack.removeLast();			}
			else if ("D".equals(current)) {
				stack.add(2*stack.peekLast());
			}
		}
		
		totalPoints = stack.stream().reduce(0, (x,y) -> x+y);
		
		return totalPoints;
	
    }
	
	private static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

}
