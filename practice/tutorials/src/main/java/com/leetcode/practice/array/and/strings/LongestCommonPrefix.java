package com.leetcode.practice.array.and.strings;

import java.util.Arrays;

/*
 * Write a function to find the longest common prefix string amongst an array of strings.

If there is no common prefix, return an empty string "".

 

Example 1:

Input: strs = ["flower","flow","flight"]
Output: "fl"
Example 2:

Input: strs = ["dog","racecar","car"]
Output: ""
Explanation: There is no common prefix among the input strings.
 */

public class LongestCommonPrefix {

	public static void main(String[] args) {
		
		String[] strs = {"flower","flow","floght"};
		LongestCommonPrefix obj = new LongestCommonPrefix();
		System.out.println(obj.longestCommonPrefix(strs));
		

	}

	public String longestCommonPrefix(String[] strs) {
		
		StringBuilder common = new StringBuilder("");
		
		if(strs.length == 1) {
			return strs[0];
		}
		
		Arrays.sort(strs);
		
		String first = strs[0];
		String last =  strs[strs.length-1];
		
		for(int i = 0; i<=first.length();i++) {
			if(first.charAt(i) != last.charAt(i)) {
				break;
			}
			common.append(first.charAt(i));
		}
		
		return common.toString();

	}

}
