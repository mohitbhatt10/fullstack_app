package com.leetcode.practice.stacks;

import java.util.ArrayDeque;
import java.util.Deque;

public class ValidParentheses {

	public static void main(String[] args) {
		String s = "(";
		ValidParentheses obj = new ValidParentheses();
		System.out.println(obj.isValid(s));

	}

	public boolean isValid(String s) {
		Deque<Character> stack = new ArrayDeque<>();
		
		for(Character ch : s.toCharArray()) {
			
			switch(ch) {
				
			case '(':
			case '{':
			case '[':
				stack.addLast(ch);
				break;
			case ')':
				if(!stack.isEmpty() && stack.peekLast() == '(') {
					stack.pollLast();
				}
				else {
					stack.addLast(ch);
				}
				break;
			case '}':	
				if(!stack.isEmpty() && stack.peekLast() == '{') {
					stack.pollLast();
				}
				else {
					stack.addLast(ch);
				}
				break;
			case ']':	
				if(!stack.isEmpty() && stack.peekLast() == '[') {
					stack.pollLast();
				}
				else {
					stack.addLast(ch);
				}
				break;
			default:
				break;
			}
		}
		
		return stack.isEmpty();
		
	}

}
