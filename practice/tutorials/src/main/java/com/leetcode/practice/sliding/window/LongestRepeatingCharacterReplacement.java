package com.leetcode.practice.sliding.window;

/*
 * You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most k times.

Return the length of the longest substring containing the same letter you can get after performing the above operations.

 

Example 1:

Input: s = "ABAB", k = 2
Output: 4
Explanation: Replace the two 'A's with two 'B's or vice versa.
Example 2:

Input: s = "AABABBA", k = 1
Output: 4
Explanation: Replace the one 'A' in the middle with 'B' and form "AABBBBA".
The substring "BBBB" has the longest repeating letters, which is 4.
There may exists other ways to achieve this answer too.
 * 
 * 
 */

public class LongestRepeatingCharacterReplacement {

	public static void main(String[] args) {
		
		String s = "AABABBA";
		System.out.println(characterReplacement(s, 2));

	}
	public static int characterReplacement2(String s, int k) {
        int[] counts = new int[26]; // Keeps the track of all characters from A to Z with their frequency in given String.
        int l = 0; // Left bound
        int longest = 0; // Resultant 
        int maxCount = 0; // max count of an individual character in the mapping-array, let's say which character is coming most of the time in the given String and its count.

        for (int r = 0; r < s.length(); r++) { // Right bound will increase with every iteration.
        	
        	// Increasing the frequency of ith character. 
        	counts[s.charAt(r) - 'A']++;
        	
            // Finding maxCount in each iteration to see which char is most occurred one till now and its value
        	maxCount = Math.max(maxCount, counts[s.charAt(r) - 'A']); 
        	
        	// Window size at ith iteration.
        	int windowSize = (r - l + 1);
        	
        	// if windowSize - maxCount > k the reduce the windowSize by increasing the left bound
            while (windowSize - maxCount > k) {
                counts[s.charAt(l) - 'A']--;
                l++; // Increasing the left bound until k < windowSize
            }
            
            //Re-Calculate windowSize again after shrinking (if required)
            windowSize = (r - l + 1);
            
            //Calculate the max between current window size vs Max till now windowSize
            longest = Math.max(longest, windowSize);
        }

        return longest;
    }
	
	public static int characterReplacement(String s, int k) {

	    int[] freq = new int[26];
	    int left = 0;
	    int maxFreq = 0;
	    int maxWindow = 0;

	    for (int right = 0; right < s.length(); right++) {

	      // Update the frequency of the current character
	      freq[s.charAt(right) - 'A']++;

	      // Update the max frequency
	      maxFreq = Math.max(maxFreq, freq[s.charAt(right) - 'A']);

	      int windowLength = right - left + 1;

	      // If the windowLength - max frequency > k,
	      // then we need to shrink the window
	      if (windowLength - maxFreq > k) {
	        freq[s.charAt(left) - 'A']--;
	        left++;
	      }

	      windowLength = right - left + 1;
	      maxWindow = Math.max(maxWindow, windowLength);
	    }

	    return maxWindow;

	  }
}
