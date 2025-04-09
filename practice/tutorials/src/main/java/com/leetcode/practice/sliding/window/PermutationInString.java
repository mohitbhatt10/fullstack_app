package com.leetcode.practice.sliding.window;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Given two strings s1 and s2, return true if s2 contains a permutation
 of s1, or false otherwise.

In other words, return true if one of s1's permutations is the substring of s2.

Example 1:

Input: s1 = "ab", s2 = "eidbaooo"
Output: true
Explanation: s2 contains one permutation of s1 ("ba").
Example 2:

Input: s1 = "ab", s2 = "eidboaoo"
Output: false
 */
public class PermutationInString {

	public static void main(String[] args) {
		
		String s1 = "ab";
		String s2 = "eidcaooo";
		
		System.out.println(checkInclusion(s1, s2));
	}

	public static boolean checkInclusion(String s1, String s2) {

		Map<Character, Integer> freqMap1 = s1.chars().mapToObj(ch -> (char) ch)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(count-> 1)));

		for (int i = 0; s2.length() >= (s1.length() + i); i++) {
			String comparingSubString = s2.substring(i,s1.length()+i);
			Map<Character, Integer> freqMap2 = comparingSubString.chars().mapToObj(ch -> (char) ch)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(count-> 1)));
			if(freqMap1.equals(freqMap2)) {
				return true;
			}
		}

		return false;
	}
}
