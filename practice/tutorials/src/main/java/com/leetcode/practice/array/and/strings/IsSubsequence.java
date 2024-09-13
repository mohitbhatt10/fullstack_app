package com.leetcode.practice.array.and.strings;

public class IsSubsequence {

	public static void main(String[] args) {
		//String s = "abc";
		//String t = "ahbgdc";
		
		String s = "axc", t = "ahbgdc";
		
		IsSubsequence obj = new IsSubsequence();
		System.out.println(obj.isSubsequence(s, t));

	}

	public boolean isSubsequence(String s, String t) {
		
		int sPointer = 0;
		int tPointer = 0;
		
		while(sPointer<s.length() && tPointer <t.length()) {
			if(t.charAt(tPointer) == s.charAt(sPointer)) {
				sPointer++;
			}
			tPointer++;
		}
		
		return sPointer == s.length();
	}

}
