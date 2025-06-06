package com.leetcode.practice.sliding.window;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Given a string s, find the length of the longest 
   substring without repeating characters.

 

Example 1:

Input: s = "abcabcbb"
Output: 3
Explanation: The answer is "abc", with the length of 3.
Example 2:

Input: s = "bbbbb"
Output: 1
Explanation: The answer is "b", with the length of 1.
Example 3:

Input: s = "pwwkew"
Output: 3
Explanation: The answer is "wke", with the length of 3.
Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
 */

public class LongestSubstringWithoutRepeatingCharacters {

	public static void main(String[] args) {
		
		String s = "pwwkew";
		System.out.println(lengthOfLongestSubstring(s));
		
	}
	
	public static int lengthOfLongestSubstring(String s) {
		
		if(s.length() == 0 || s.length() ==1) {
			return s.length();
		}
		
		int start = 0;
		List<Character> list = new CopyOnWriteArrayList<>();
		int max = 0;
		
		
		for(int i=0 ; i< s.length(); i++) {
			if(!list.contains(s.charAt(i))) {
				list.add(s.charAt(i));
			}
			else {
				for(Character ch : list) {
					start++;
					if(ch == s.charAt(i)) {
						list.remove(ch);
						list.add(ch);
						break;
					}
					else {
						list.remove(ch);
					}
				}
			}
			max = Math.max(max, i-start +1);
		}
		return max;
    }
	
	public static int lengthOfLongestSubstring2(String s) {
        HashSet<Character> set = new HashSet<>();
        int l = 0;
        int longest = 0;

        for (int r = 0; r < s.length(); r++) {
            while (set.contains(s.charAt(r))) {
                set.remove(s.charAt(l));
                l++;
            }

            longest = Math.max(longest, r - l + 1);
            set.add(s.charAt(r));
        }
        return longest;
    }

}
