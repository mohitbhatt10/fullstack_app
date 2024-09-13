package com.leetcode.practice.array.and.strings;

public class MergeStringsAlternately {

	public static void main(String[] args) {

		String word1 = "abc";
		String word2 = "efghi";
		MergeStringsAlternately obj = new MergeStringsAlternately();
		System.out.println(obj.mergeAlternately(word1, word2));

	}

	public String mergeAlternately(String word1, String word2) {
		
		StringBuilder merged = new StringBuilder();
		
		for(int i=0,j=0; i<word1.length() || j<word2.length() ;i++, j++) {
			
			if(i < word1.length()) {
				merged.append(word1.charAt(i));
			}
			if( j < word2.length()) {
				merged.append(word2.charAt(j));
			}
		}
		
		return merged.toString();
	}

}
