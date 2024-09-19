package com.leetcode.practice.stacks;

import java.util.Stack;

public class EvaluateReversePolishNotation {

	public static void main(String[] args) {
		
		String[] tokens = {"10","6","9","3","+","-11","*","/","*","17","+","5","+"};
		EvaluateReversePolishNotation obj = new EvaluateReversePolishNotation();
		System.out.println(obj.evalRPN(tokens));

	}
	
	public int evalRPN(String[] tokens) {
        
		Stack<Integer> stack = new Stack<>();
		int b=0;
		int a=0;
		
		for(String token : tokens) {
			
			switch(token) {
				
			case "+":
				b = stack.pop();
				a = stack.pop();
				int sum = a+b;
				stack.push(sum);
				break;
			case "*":
				b = stack.pop();
				a = stack.pop();
				int product = a*b;
				stack.push(product);
				break;
			case "-":
				b = stack.pop();
				a = stack.pop();
				int sub = a-b;
				stack.push(sub);
				break;
			case "/":
				b = stack.pop();
				a = stack.pop();
				double division = (double) a / b;
                stack.push(division < 0 ? (int) Math.ceil(division) : (int) Math.floor(division));
				break;
			default:
				stack.push(Integer.parseInt(token));
				break;
			}
		}
		return stack.pop();
    }

}
