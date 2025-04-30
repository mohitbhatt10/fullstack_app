package com.leetcode.practice.recurcise.backtracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent. Return the answer in any order.

A mapping of digits to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.


 

Example 1:

Input: digits = "23"
Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
Example 2:

Input: digits = ""
Output: []
Example 3:

Input: digits = "2"
Output: ["a","b","c"]
 */

public class LetterCombinationsOfAPhoneNumber {
	

	public static void main(String[] args) {
		
		String digits = "23";
		
		LetterCombinationsOfAPhoneNumber obj = new LetterCombinationsOfAPhoneNumber();
		
		System.out.println(obj.letterCombinations(digits));

	}
	
	public List<String> letterCombinations(String digits) {
		
		Map<Character, String> map = new HashMap<>();
		map.put('2', "abc");
		map.put('3', "def");
		map.put('4', "ghi");
		map.put('5', "jkl");
		map.put('6', "mno");
		map.put('7', "pqrs");
		map.put('8', "tuv");
		map.put('9', "wxyz");
      
		List<String> result = new ArrayList<>();
		
		backtrack(new StringBuilder(), result, digits, 0, map);
		
		return result;
    }
	
	public void backtrack(StringBuilder combination, List<String> result, String digits, int index, Map<Character, String> map) {
		
		if(combination.length() == digits.length()) { // base case when combination length is equal to the digits length
			if(combination.length() != 0) {
				result.add(combination.toString());
			}
			return;
		}
		
		String possibleAlphabetsForADigit = map.get(digits.charAt(index)); // Get the corresponding string based on the number entered
		
		for(int i=0 ; i< possibleAlphabetsForADigit.length(); i++) {
			combination.append(possibleAlphabetsForADigit.charAt(i)); // Add
			backtrack(combination, result, digits, index+1, map); // Recursion
			combination.deleteCharAt(combination.length()-1); // BackTracking
		}
	}

}
